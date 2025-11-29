import { useState, ChangeEvent, FormEvent } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/components/ui/toast";

type ChangePasswordDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
};

type FormState = {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
};

type FormErrors = Partial<FormState>;

const PASSWORD_POLICY =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;

export function ChangePasswordDialog({
  open,
  onOpenChange,
}: ChangePasswordDialogProps) {
  const { changePassword } = useAuth();
  const { addToast } = useToast();

  const [form, setForm] = useState<FormState>({
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitting, setSubmitting] = useState(false);

  const resetState = () => {
    setForm({
      currentPassword: "",
      newPassword: "",
      confirmNewPassword: "",
    });
    setErrors({});
    setSubmitting(false);
  };

  const handleOpenChange = (nextOpen: boolean) => {
    if (!nextOpen) {
      resetState();
    }
    onOpenChange(nextOpen);
  };

  const setField =
    (field: keyof FormState) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({ ...prev, [field]: event.target.value }));
    };

  const validate = (): boolean => {
    const nextErrors: FormErrors = {};

    if (!form.currentPassword.trim()) {
      nextErrors.currentPassword = "Vui lòng nhập mật khẩu hiện tại.";
    }
    if (!form.newPassword.trim()) {
      nextErrors.newPassword = "Vui lòng nhập mật khẩu mới.";
    } else if (!PASSWORD_POLICY.test(form.newPassword)) {
      nextErrors.newPassword =
        "Mật khẩu mới phải dài 8-20 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.";
    }
    if (!form.confirmNewPassword.trim()) {
      nextErrors.confirmNewPassword = "Vui lòng xác nhận mật khẩu mới.";
    } else if (form.newPassword !== form.confirmNewPassword) {
      nextErrors.confirmNewPassword = "Mật khẩu xác nhận không khớp.";
    }
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!validate() || submitting) return;

    setSubmitting(true);
    try {
      await changePassword({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
      });
      addToast({
        title: "Đổi mật khẩu thành công",
        description: "Mật khẩu của bạn đã được cập nhật.",
        variant: "success",
      });
      handleOpenChange(false);
    } catch (error: any) {
      const message =
        error?.response?.data?.message ||
        error?.message ||
        "Không thể đổi mật khẩu. Vui lòng thử lại.";
      addToast({
        title: "Đổi mật khẩu thất bại",
        description: message,
        variant: "destructive",
      });

      if (
        error?.response?.data?.errorCode === "INVALID_CURRENT_PASSWORD" ||
        message.toLowerCase().includes("current")
      ) {
        setErrors((prev) => ({
          ...prev,
          currentPassword: "Mật khẩu hiện tại không đúng.",
        }));
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Đổi mật khẩu</DialogTitle>
          <DialogDescription>
            Nhập mật khẩu hiện tại và đặt mật khẩu mới để bảo vệ tài khoản của
            bạn.
          </DialogDescription>
        </DialogHeader>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="text-sm font-medium text-gray-700">
              Mật khẩu hiện tại
            </label>
            <Input
              type="password"
              placeholder="Nhập mật khẩu hiện tại"
              value={form.currentPassword}
              onChange={setField("currentPassword")}
              autoComplete="current-password"
            />
            {errors.currentPassword && (
              <p className="mt-1 text-xs text-red-600">{errors.currentPassword}</p>
            )}
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">
              Mật khẩu mới
            </label>
            <Input
              type="password"
              placeholder="Tạo mật khẩu mới"
              value={form.newPassword}
              onChange={setField("newPassword")}
              autoComplete="new-password"
            />
            {errors.newPassword && (
              <p className="mt-1 text-xs text-red-600">{errors.newPassword}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">
              Mật khẩu phải 8-20 ký tự, gồm chữ thường, chữ hoa, số và ký tự đặc
              biệt.
            </p>
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">
              Xác nhận mật khẩu mới
            </label>
            <Input
              type="password"
              placeholder="Nhập lại mật khẩu mới"
              value={form.confirmNewPassword}
              onChange={setField("confirmNewPassword")}
              autoComplete="new-password"
            />
            {errors.confirmNewPassword && (
              <p className="mt-1 text-xs text-red-600">{errors.confirmNewPassword}</p>
            )}
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
              disabled={submitting}
            >
              Hủy
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Đang lưu..." : "Lưu mật khẩu"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

