import { useState } from "react";
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
import { CreateUserDto, UserRole } from "@/types/user.type";

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
  const [formData, setFormData] = useState<CreateUserDto>({
    firstName: "",
    lastName: "",
    email: "",
    role: UserRole.STUDENT,
    studentId: "",
    instructorId: ""
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.firstName.trim()) {
      newErrors.firstName = "First name is required";
    }
    if (!formData.lastName.trim()) {
      newErrors.lastName = "Last name is required";
    }
    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Invalid email format";
    }
    if (formData.role === UserRole.STUDENT && !formData.studentId?.trim()) {
      newErrors.studentId = "Student ID is required";
    }
    if (formData.role === UserRole.INSTRUCTOR && !formData.instructorId?.trim()) {
      newErrors.instructorId = "Instructor ID is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    setLoading(true);
    try {
      await onSubmit(formData);
      // Reset form
      setFormData({
        firstName: "",
        lastName: "",
        email: "",
        role: UserRole.STUDENT,
        studentId: "",
        instructorId: ""
      });
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
            Enter user details to create a new account. Password will be auto-generated and sent via email.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <label htmlFor="firstName" className="text-sm font-medium">
                First Name <span className="text-red-500">*</span>
              </label>
              <Input
                id="firstName"
                value={formData.firstName}
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                placeholder="John"
                className={errors.firstName ? "border-red-500" : ""}
              />
              {errors.firstName && (
                <p className="text-xs text-red-500">{errors.firstName}</p>
              )}
            </div>

            <div className="space-y-2">
              <label htmlFor="lastName" className="text-sm font-medium">
                Last Name <span className="text-red-500">*</span>
              </label>
              <Input
                id="lastName"
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                placeholder="Doe"
                className={errors.lastName ? "border-red-500" : ""}
              />
              {errors.lastName && (
                <p className="text-xs text-red-500">{errors.lastName}</p>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <label htmlFor="email" className="text-sm font-medium">
              Email <span className="text-red-500">*</span>
            </label>
            <Input
              id="email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              placeholder="john.doe@university.edu"
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
              value={formData.role}
              onValueChange={(value) => setFormData({ ...formData, role: value as UserRole })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={UserRole.STUDENT}>Student</SelectItem>
                <SelectItem value={UserRole.INSTRUCTOR}>Instructor</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {formData.role === UserRole.STUDENT && (
            <div className="space-y-2">
              <label htmlFor="studentId" className="text-sm font-medium">
                Student ID <span className="text-red-500">*</span>
              </label>
              <Input
                id="studentId"
                value={formData.studentId}
                onChange={(e) => setFormData({ ...formData, studentId: e.target.value })}
                placeholder="SE123456"
                className={errors.studentId ? "border-red-500" : ""}
              />
              {errors.studentId && (
                <p className="text-xs text-red-500">{errors.studentId}</p>
              )}
            </div>
          )}

          {formData.role === UserRole.INSTRUCTOR && (
            <div className="space-y-2">
              <label htmlFor="instructorId" className="text-sm font-medium">
                Instructor ID <span className="text-red-500">*</span>
              </label>
              <Input
                id="instructorId"
                value={formData.instructorId}
                onChange={(e) => setFormData({ ...formData, instructorId: e.target.value })}
                placeholder="INS001"
                className={errors.instructorId ? "border-red-500" : ""}
              />
              {errors.instructorId && (
                <p className="text-xs text-red-500">{errors.instructorId}</p>
              )}
            </div>
          )}
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

