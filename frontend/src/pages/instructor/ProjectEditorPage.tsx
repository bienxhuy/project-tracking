import { useState, useEffect } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { toast } from "sonner"
import { ArrowLeft, Save } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"

import { StudentSelector } from "@/components/StudentSelector"
import { BaseUser } from "@/types/user.type"
import { CreateProjectRequest, UpdateProjectRequest } from "@/types/project.type"
import { projectService } from "@/services/project.service"
import { fetchAllYears, fetchAllFaculties } from "@/services/semester.service"
import { projectSchema } from "@/zod_schema/project.schema"

type ProjectFormValues = z.infer<typeof projectSchema>

export const ProjectEditorPage = () => {
  const navigate = useNavigate()
  const { projectId } = useParams<{ projectId: string }>()
  // Determine if we are in edit mode based on the presence of a projectId
  const isEditMode = !!projectId

  const [availableYears, setAvailableYears] = useState<number[]>([])
  const [availableFaculties, setAvailableFaculties] = useState<string[]>([])
  const [selectedStudents, setSelectedStudents] = useState<BaseUser[]>([])
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  // Form setup with Zod validation
  const form = useForm<ProjectFormValues>({
    resolver: zodResolver(projectSchema),
    defaultValues: {
      title: "",
      objectives: "",
      content: "",
      year: 0,
      semester: 0,
      batch: "",
      falculty: "",
      startDate: "",
      endDate: "",
      studentIds: [],
    },
  });

  // Load available years, faculties and project data if in edit mode
  useEffect(() => {
    const loadData = async () => {
      const years = fetchAllYears()
      setAvailableYears(years)

      const faculties = fetchAllFaculties()
      setAvailableFaculties(faculties)

      // Load project data if in edit mode
      if (isEditMode) {
        await loadProjectData()
      }

      setIsLoading(false)
    }

    loadData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectId])

  // Function to load project data into the form for editing
  // This function is used in editing mode only
  const loadProjectData = async () => {
    try {
      const response = await projectService.getProjectById(parseInt(projectId!))
      
      if (response.status === "success" && response.data) {
        const projectDetail = response.data

        // Set form values individually to ensure proper type handling
        form.setValue('title', projectDetail.title)
        form.setValue('objectives', projectDetail.objectives)
        form.setValue('content', projectDetail.content)
        form.setValue('year', projectDetail.year)
        form.setValue('semester', projectDetail.semester)
        form.setValue('batch', projectDetail.batch)
        form.setValue('falculty', projectDetail.falculty)
        form.setValue('startDate', new Date(projectDetail.startDate).toISOString().split('T')[0])
        form.setValue('endDate', new Date(projectDetail.endDate).toISOString().split('T')[0])
        form.setValue('studentIds', projectDetail.students.map((s) => s.id))

        // Load selected students from project detail
        setSelectedStudents(projectDetail.students)
      } else {
        toast.error(response.message || "Không tìm thấy dự án")
        navigate("/instructor/dashboard")
      }
    } catch (error) {
      toast.error("Không thể tải thông tin dự án")
      navigate("/instructor/dashboard")
    }
  }

  // Watch studentIds changes from StudentSelector
  useEffect(() => {
    form.setValue("studentIds", selectedStudents.map((s) => s.id))
  }, [selectedStudents, form])

  // Form submission handler
  // Served for both editing and creating mode
  const handleSubmit = async (data: ProjectFormValues) => {
    setIsSubmitting(true)

    try {
      if (isEditMode) {
        const updateData: UpdateProjectRequest = {
          title: data.title,
          objectives: data.objectives,
          content: data.content,
          year: data.year,
          semester: data.semester,
          batch: data.batch,
          falculty: data.falculty,
          startDate: new Date(data.startDate),
          endDate: new Date(data.endDate),
          studentIds: data.studentIds,
        }
        const response = await projectService.updateProject(parseInt(projectId!), updateData)
        console.log("✅ Update Project Response:", response)
        
        // Check for success (case-insensitive)
        if (response.status?.toLowerCase() === "success") {
          toast.success("Cập nhật dự án thành công")
          navigate("/instructor/dashboard")
        } else {
          console.warn("⚠️ Update response status is not 'success':", response.status)
          toast.error(response.message || "Cập nhật dự án thất bại")
        }
      } else {
        const createData: CreateProjectRequest = {
          title: data.title,
          objectives: data.objectives,
          content: data.content,
          year: data.year,
          semester: data.semester,
          batch: data.batch,
          falculty: data.falculty,
          startDate: new Date(data.startDate),
          endDate: new Date(data.endDate),
          studentIds: data.studentIds,
        }
        const response = await projectService.createProject(createData)
        console.log("✅ Create Project Response:", response)
        console.log("✅ Response Status:", response.status)
        console.log("✅ Response Status Type:", typeof response.status)
        console.log("✅ Full Response Object:", JSON.stringify(response, null, 2))
        
        // Check for success (case-insensitive)
        if (response.status?.toLowerCase() === "success") {
          toast.success("Tạo dự án thành công")
          navigate("/instructor/dashboard")
        } else {
          console.warn("⚠️ Response status is not 'success':", response.status)
          toast.error(response.message || "Tạo dự án thất bại")
        }
      }
    } catch (error) {
      console.error("❌ Create Project Error:", error)
      toast.error(isEditMode ? "Cập nhật dự án thất bại" : "Tạo dự án thất bại")
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCancel = () => {
    navigate("/instructor/dashboard")
  }

  return (
    <div className="container mx-auto py-6 px-4 max-w-5xl">
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" size="icon" onClick={handleCancel}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold">
            {isEditMode ? "Cập nhật dự án" : "Tạo dự án mới"}
          </h1>
          <p className="text-muted-foreground">
            {isEditMode
              ? "Chỉnh sửa thông tin dự án của bạn"
              : "Điền thông tin để tạo dự án mới"}
          </p>
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center py-12">
          <p className="text-muted-foreground">Đang tải...</p>
        </div>
      ) : (
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
            {/* Basic Information */}
            <Card>
              <CardHeader>
                <CardTitle>Thông tin cơ bản</CardTitle>
                <CardDescription>Các trường đánh dấu (*) là bắt buộc</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Project Name */}
                <FormField
                  control={form.control}
                  name="title"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>
                        Tên dự án <span className="text-destructive">*</span>
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="Nhập tên dự án..." {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Objectives */}
                <FormField
                  control={form.control}
                  name="objectives"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>
                        Mục tiêu <span className="text-destructive">*</span>
                      </FormLabel>
                      <FormControl>
                        <Textarea
                          placeholder="Mô tả mục tiêu của dự án..."
                          rows={3}
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Description */}
                <FormField
                  control={form.control}
                  name="content"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>
                        Mô tả <span className="text-destructive">*</span>
                      </FormLabel>
                      <FormControl>
                        <Textarea
                          placeholder="Mô tả chi tiết về dự án..."
                          rows={4}
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Academic Information Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  {/* Year */}
                  <FormField
                    control={form.control}
                    name="year"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Năm học <span className="text-destructive">*</span>
                        </FormLabel>
                        <Select
                          onValueChange={(value) => field.onChange(parseInt(value))}
                          value={field.value > 0 ? field.value.toString() : ""}
                        >
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Chọn năm" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {availableYears.map((year) => (
                              <SelectItem key={year} value={year.toString()}>
                                {year}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Semester */}
                  <FormField
                    control={form.control}
                    name="semester"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Học kỳ <span className="text-destructive">*</span>
                        </FormLabel>
                        <Select
                          onValueChange={(value) => field.onChange(parseInt(value))}
                          value={field.value > 0 ? field.value.toString() : ""}
                        >
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Chọn học kỳ" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="1">1</SelectItem>
                            <SelectItem value="2">2</SelectItem>
                            <SelectItem value="3">3</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Batch */}
                  <FormField
                    control={form.control}
                    name="batch"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Đợt <span className="text-destructive">*</span>
                        </FormLabel>
                        <Select
                          onValueChange={field.onChange}
                          value={field.value || ""}
                        >
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Chọn đợt" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="1">1</SelectItem>
                            <SelectItem value="2">2</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Faculty */}
                  <FormField
                    control={form.control}
                    name="falculty"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Khoa <span className="text-destructive">*</span>
                        </FormLabel>
                        <Select onValueChange={field.onChange} value={field.value || ""}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Chọn khoa" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {availableFaculties.map((faculty) => (
                              <SelectItem key={faculty} value={faculty}>
                                {faculty}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                {/* Date Information Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Start Date */}
                  <FormField
                    control={form.control}
                    name="startDate"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Ngày bắt đầu <span className="text-destructive">*</span>
                        </FormLabel>
                        <FormControl>
                          <Input
                            type="date"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* End Date */}
                  <FormField
                    control={form.control}
                    name="endDate"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>
                          Ngày kết thúc <span className="text-destructive">*</span>
                        </FormLabel>
                        <FormControl>
                          <Input
                            type="date"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
              </CardContent>
            </Card>

            {/* Participating Students */}
            <Card>
              <CardHeader>
                <CardTitle>
                  Sinh viên tham gia <span className="text-destructive">*</span>
                </CardTitle>
                <CardDescription>
                  Tìm kiếm và chọn sinh viên tham gia dự án
                </CardDescription>
              </CardHeader>
              <CardContent>
                <FormField
                  control={form.control}
                  name="studentIds"
                  render={() => (
                    <FormItem>
                      <FormControl>
                        <StudentSelector
                          selectedStudents={selectedStudents}
                          onStudentsChange={setSelectedStudents}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </CardContent>
            </Card>

            <Separator />

            {/* Action Buttons */}
            <div className="flex justify-end gap-4">
              <Button type="button" variant="outline" onClick={handleCancel}>
                Hủy
              </Button>
              <Button type="submit" disabled={isSubmitting} className="gap-2">
                <Save className="h-4 w-4" />
                {isSubmitting
                  ? "Đang xử lý..."
                  : isEditMode
                    ? "Cập nhật dự án"
                    : "Tạo dự án"}
              </Button>
            </div>
          </form>
        </Form>
      )}
    </div>
  )
}
