# 🔍 Cách Xem Spring CGLIB Proxy Classes

## 📖 Tại Sao Không Thấy File `.java` hoặc `.class`?

Spring tạo proxy classes **động trong memory** khi runtime, KHÔNG tạo file source code hoặc bytecode ra disk!

```
❌ KHÔNG CÓ:
- src/generated/ProjectServiceImpl$$EnhancerBySpringCGLIB.java
- target/classes/ProjectServiceImpl$$EnhancerBySpringCGLIB.class

✅ CHỈ TỒN TẠI TRONG MEMORY:
- JVM Heap Memory → Proxy Class Definition
```

---

## 🛠️ Cách 1: Runtime Inspection (Đã Implement)

### Code Đã Thêm Vào `ProjectServiceImpl.java`

```java
@PostConstruct
public void inspectProxyDetails() {
    Object bean = context.getBean(ProjectServiceImpl.class);
    
    System.out.println("Bean class name: " + bean.getClass().getName());
    // Output: ProjectServiceImpl$$EnhancerBySpringCGLIB$$a1b2c3d4
    
    System.out.println("Is CGLIB proxy? " + AopUtils.isCglibProxy(bean));
    // Output: true
}
```

### Chạy Application

```bash
cd /home/truong/IdeaProjects/project-tracking/backend
mvn spring-boot:run
```

### Expected Output

```
========================================
🔍 SPRING PROXY INSPECTION
========================================

📦 CLASS INFORMATION:
Bean class name: POSE_Project_Tracking.Blog.service.impl.ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd
Simple name: ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd
Superclass: POSE_Project_Tracking.Blog.service.impl.ProjectServiceImpl

🎭 PROXY DETECTION:
Is AOP proxy? true
Is CGLIB proxy? true
Is JDK dynamic proxy? false

🔌 IMPLEMENTED INTERFACES:
  - org.springframework.aop.SpringProxy
  - org.springframework.aop.framework.Advised
  - org.springframework.cglib.proxy.Factory

⚙️ METHODS COUNT:
Original class methods: 15
Proxy class methods: 45  ← Nhiều hơn rất nhiều!

🔍 SAMPLE METHOD INSPECTION (getProjectById):
  Method: getProjectById
  Declaring class: ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd
  Parameters: [class java.lang.Long]

🎯 TARGET CLASS (Real Bean):
Target class: POSE_Project_Tracking.Blog.service.impl.ProjectServiceImpl

========================================
✅ INSPECTION COMPLETE
========================================
```

---

## 🛠️ Cách 2: Dump Bytecode to File

### Bước 1: Thêm JVM Arguments

Chỉnh sửa Maven plugin trong `pom.xml`:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>
            -Dcglib.debugLocation=/home/truong/IdeaProjects/project-tracking/backend/cglib-debug
            -Dnet.sf.cglib.core.DebuggingClassWriter.traceEnabled=true
        </jvmArguments>
    </configuration>
</plugin>
```

### Bước 2: Tạo Folder

```bash
mkdir -p /home/truong/IdeaProjects/project-tracking/backend/cglib-debug
```

### Bước 3: Chạy Application

```bash
mvn spring-boot:run
```

### Bước 4: Kiểm Tra Output

```bash
ls -la /home/truong/IdeaProjects/project-tracking/backend/cglib-debug/

# Expected:
# ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd.class
# ProjectServiceImpl$$FastClassBySpringCGLIB$$98xy76zw.class
```

### Bước 5: Decompile `.class` Files

```bash
# Install decompiler
sudo apt install -y cfr  # hoặc dùng JD-GUI

# Decompile
cd /home/truong/IdeaProjects/project-tracking/backend/cglib-debug
cfr ProjectServiceImpl\$\$EnhancerBySpringCGLIB*.class > proxy.java

# View
cat proxy.java
```

---

## 🛠️ Cách 3: Java Debugger

### Bước 1: Set Breakpoint

Trong IntelliJ IDEA:
1. Mở `ProjectServiceImpl.java`
2. Set breakpoint tại dòng đầu tiên của `getProjectById()`

### Bước 2: Debug Mode

```bash
# Run in debug mode
mvn spring-boot:run -Dspring-boot.run.fork=false
```

### Bước 3: Inspect Variables

Khi breakpoint hit:
1. Xem biến `this`
2. Click "View as" → "Evaluate Expression"
3. Type: `this.getClass().getName()`
4. Result: `ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd`

### Bước 4: Evaluate Expressions

```java
// In debugger console
this.getClass().getMethods()  // See all proxy methods
this.getClass().getSuperclass()  // See original class
```

---

## 🛠️ Cách 4: JVM Attach with JConsole

### Bước 1: Tìm PID

```bash
# Start application
mvn spring-boot:run &

# Get PID
jps -l | grep ProjectTrackingApplication
# Output: 12345 POSE_Project_Tracking.Blog.ProjectTrackingApplication
```

### Bước 2: Use JConsole

```bash
jconsole 12345
```

### Bước 3: Navigate

1. Tab "MBeans"
2. Expand "org.springframework.boot"
3. Look for proxy classes

---

## 🛠️ Cách 5: Save Proxy Bytecode với Custom Code

### Create Utility Class

```java
package POSE_Project_Tracking.Blog.util;

import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Component
public class ProxyBytecodeWriter {
    
    @PostConstruct
    public void enableCglibDebug() {
        // Set CGLIB debug location
        String debugPath = System.getProperty("user.dir") + "/cglib-classes";
        
        // Create directory
        new File(debugPath).mkdirs();
        
        // Enable CGLIB debugging
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, debugPath);
        
        System.out.println("📁 CGLIB proxy classes will be saved to: " + debugPath);
    }
}
```

### Run Application

```bash
mvn spring-boot:run
```

### Check Output

```bash
ls -la cglib-classes/
# Will contain .class files of all CGLIB proxies
```

---

## 🛠️ Cách 6: Use Spring Boot Actuator

### Bước 1: Add Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Bước 2: Enable Endpoints

```properties
# application.properties
management.endpoints.web.exposure.include=beans,mappings
```

### Bước 3: Query Bean Info

```bash
# Start application
mvn spring-boot:run

# Check beans
curl http://localhost:9090/actuator/beans | jq '.contexts.application.beans | 
  with_entries(select(.key | contains("ProjectService")))'
```

### Output

```json
{
  "projectServiceImpl": {
    "aliases": [],
    "scope": "singleton",
    "type": "POSE_Project_Tracking.Blog.service.impl.ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd",
    "resource": "file [ProjectServiceImpl.class]",
    "dependencies": [
      "projectRepository",
      "userRepository",
      "taskRepository",
      "projectMapper",
      "securityUtil"
    ]
  }
}
```

---

## 🔬 Analyzing Proxy Class Structure

### Expected Proxy Class (Conceptual)

```java
// CGLIB generates something like this (simplified)
public class ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd 
        extends ProjectServiceImpl 
        implements SpringProxy, Advised, Factory {
    
    // CGLIB fields
    private MethodInterceptor CGLIB$CALLBACK_0;  // Transaction interceptor
    private MethodInterceptor CGLIB$CALLBACK_1;  // Cache interceptor
    private static Method CGLIB$getProjectById$0$Method;
    private static MethodProxy CGLIB$getProjectById$0$Proxy;
    
    // Static initializer
    static {
        CGLIB$STATICHOOK1();
    }
    
    private static void CGLIB$STATICHOOK1() {
        CGLIB$getProjectById$0$Method = 
            ReflectUtils.findMethods(new String[] {
                "getProjectById", 
                "(Ljava/lang/Long;)LPOSE_Project_Tracking/Blog/dto/res/ProjectRes;"
            }, ProjectServiceImpl.class.getDeclaredMethods())[0];
        
        CGLIB$getProjectById$0$Proxy = 
            MethodProxy.create(
                ProjectServiceImpl.class, 
                ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd.class, 
                "(Ljava/lang/Long;)LPOSE_Project_Tracking/Blog/dto/res/ProjectRes;", 
                "getProjectById", 
                "CGLIB$getProjectById$0"
            );
    }
    
    // Proxy method (intercepts calls)
    @Override
    public final ProjectRes getProjectById(Long id) {
        // Interceptor chain (Transaction → Cache → Original)
        MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
        if (interceptor == null) {
            CGLIB$BIND_CALLBACKS(this);
            interceptor = this.CGLIB$CALLBACK_0;
        }
        
        if (interceptor != null) {
            // Call interceptor chain
            return (ProjectRes) interceptor.intercept(
                this, 
                CGLIB$getProjectById$0$Method, 
                new Object[] { id }, 
                CGLIB$getProjectById$0$Proxy
            );
        } else {
            // Fallback to super (original method)
            return super.getProjectById(id);
        }
    }
    
    // Original method renamed (for callback)
    final ProjectRes CGLIB$getProjectById$0(Long id) {
        return super.getProjectById(id);
    }
    
    // ... many more CGLIB methods ...
    
    // Factory methods
    public void setCallback(int index, Callback callback) { ... }
    public void setCallbacks(Callback[] callbacks) { ... }
    public Callback getCallback(int index) { ... }
    
    // ... 30+ more generated methods ...
}
```

---

## 📊 Comparison: Original vs Proxy

| Aspect | Original Class | CGLIB Proxy Class |
|--------|---------------|-------------------|
| **Name** | `ProjectServiceImpl` | `ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd` |
| **Package** | `...service.impl` | Same |
| **Superclass** | N/A | `ProjectServiceImpl` |
| **Methods** | 15 methods | 45+ methods |
| **Interfaces** | `IProjectService` | `IProjectService`, `SpringProxy`, `Advised`, `Factory` |
| **Source** | Your code | CGLIB generated |
| **Location** | `src/main/java/...` | JVM memory only |
| **Size** | ~5 KB | ~15 KB |

---

## 🎓 Key Takeaways

### 1. Runtime Generation
- Proxy classes are **generated at runtime**
- Not compiled from `.java` source files
- Created using CGLIB bytecode manipulation

### 2. Memory Only
- Exist only in **JVM heap memory**
- Can be dumped to disk with special config
- Not part of normal build output

### 3. Naming Convention
```
Original:  ProjectServiceImpl
Proxy:     ProjectServiceImpl$$EnhancerBySpringCGLIB$$<random_hash>
Fast Class: ProjectServiceImpl$$FastClassBySpringCGLIB$$<random_hash>
```

### 4. Method Interception
- Every public method is **overridden**
- Original methods renamed to `CGLIB$methodName$0`
- Interceptor chain handles annotations

### 5. File Locations

```
❌ NOT HERE:
/home/truong/IdeaProjects/project-tracking/backend/target/generated-sources/
/home/truong/IdeaProjects/project-tracking/backend/target/classes/

✅ DUMP TO HERE (if enabled):
/home/truong/IdeaProjects/project-tracking/backend/cglib-debug/
/home/truong/IdeaProjects/project-tracking/backend/cglib-classes/
```

---

## 🚀 Quick Test

### Run Inspection

```bash
cd /home/truong/IdeaProjects/project-tracking/backend
mvn clean spring-boot:run
```

### Look for Output

```
========================================
🔍 SPRING PROXY INSPECTION
========================================
Bean class name: ProjectServiceImpl$$EnhancerBySpringCGLIB$$12ab34cd
...
```

### Optional: Enable Bytecode Dump

```bash
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dcglib.debugLocation=./cglib-output"

# Check files
ls -la cglib-output/
```

---

## 📚 Additional Resources

- [CGLIB Documentation](https://github.com/cglib/cglib/wiki)
- [Spring AOP Proxies](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-proxying)
- [JVM Bytecode Inspection Tools](https://en.wikipedia.org/wiki/List_of_Java_bytecode_instructions)

---

**Created**: November 15, 2025  
**Purpose**: Debug and inspect Spring CGLIB proxy classes  
**Status**: ✅ Ready for testing
