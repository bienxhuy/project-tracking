import { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { CreateUserDto } from "@/types/user.type";
import { UserRole, LoginType } from "@/types/util.type";

// Role mapping for the form
type FormRole = "STUDENT" | "INSTRUCTOR";

interface CreateUserDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (data: CreateUserDto) => Promise<void>;
}

export function CreateUserDialog({
  open,
  onOpenChange,
  onSubmit
}: CreateUserDialogProps) {
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState<FormRole>("STUDENT");
  const [generatedPassword, setGeneratedPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Generate random password when dialog opens
  useEffect(() => {
    if (open) {
      generatePassword();
    }
  }, [open]);

  const generatePassword = () => {
    const length = 12;
    const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
    let password = "";
    for (let i = 0; i < length; i++) {
      password += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    setGeneratedPassword(password);
  };

  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (!displayName.trim()) {
      newErrors.displayName = "Name is required";
    }
    if (!email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = "Invalid email format";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    setLoading(true);
    try {
      // Generate username from email (part before @)
      const username = email.split("@")[0].toLowerCase().replace(/[^a-z0-9]/g, "");

      // Map form role to backend role
      const backendRole = role === "STUDENT" ? UserRole.STUDENT : UserRole.INSTRUCTOR;

      const userData: CreateUserDto = {
        username: username,
        password: generatedPassword,
        email: email,
        displayName: displayName,
        role: backendRole,
        loginType: LoginType.LOCAL
      };

      await onSubmit(userData);
      
      // Show success message about verification email
      // Note: Backend automatically sends OTP verification email
      // alert(`✅ User created successfully!\n\n📧 A verification email with OTP code has been sent to:\n${email}\n\nThe user must verify their email before they can log in.`);
      
      // Reset form
      setDisplayName("");
      setEmail("");
      setRole("STUDENT");
      setGeneratedPassword("");
      setErrors({});
      onOpenChange(false);
    } catch (error) {
      console.error("Failed to create user:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Create New User Account</DialogTitle>
          <DialogDescription>
            Enter user details. The system will automatically send a verification email with OTP code to activate the account.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-4">
          <div className="space-y-2">
            <label htmlFor="displayName" className="text-sm font-medium">
              Name <span className="text-red-500">*</span>
            </label>
            <Input
              id="displayName"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="Nguyễn Văn An"
              className={errors.displayName ? "border-red-500" : ""}
            />
            {errors.displayName && (
              <p className="text-xs text-red-500">{errors.displayName}</p>
            )}
          </div>

          <div className="space-y-2">
            <label htmlFor="email" className="text-sm font-medium">
              Email <span className="text-red-500">*</span>
            </label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="nguyen.van.an@ute.edu.vn"
              className={errors.email ? "border-red-500" : ""}
            />
            {errors.email && (
              <p className="text-xs text-red-500">{errors.email}</p>
            )}
          </div>

          <div className="space-y-2">
            <label htmlFor="role" className="text-sm font-medium">
              Role <span className="text-red-500">*</span>
            </label>
            <Select
              value={role}
              onValueChange={(value) => setRole(value as FormRole)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="STUDENT">Student</SelectItem>
                <SelectItem value="INSTRUCTOR">Instructor</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Show generated password and email notification info */}
          <div className="space-y-3">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
              <p className="text-sm text-blue-800">
                <span className="font-semibold">Generated Password:</span>
              </p>
              <div className="mt-2 flex items-center gap-2">
                <Input
                  value={generatedPassword}
                  readOnly
                  className="text-xs font-mono bg-white"
                  type="text"
                />
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={generatePassword}
                  className="whitespace-nowrap"
                >
                  Regenerate
                </Button>
              </div>
            </div>
            
            <div className="bg-green-50 border border-green-200 rounded-lg p-3">
              <p className="text-sm text-green-800">
                <span className="font-semibold">📧 Email Verification:</span><br/>
                After creating, the system will automatically send a verification email with OTP code to the user's email address. The user must login to verify the account.
              </p>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={loading}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? "Creating..." : "Create User"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
