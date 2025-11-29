import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Bell, BellOff } from 'lucide-react';

interface NotificationPermissionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onRequestPermission: () => Promise<void>;
}

export const NotificationPermissionDialog = ({
  open,
  onOpenChange,
  onRequestPermission,
}: NotificationPermissionDialogProps) => {
  const [isRequesting, setIsRequesting] = useState(false);

  const handleAllow = async () => {
    setIsRequesting(true);
    try {
      await onRequestPermission();
      // Close dialog after successful permission
      onOpenChange(false);
    } catch (error) {
      console.error('Failed to request permission:', error);
    } finally {
      setIsRequesting(false);
    }
  };

  const handleSkip = () => {
    // Mark as skipped in localStorage
    localStorage.setItem('notificationPermissionSkipped', 'true');
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <div className="flex items-center justify-center mb-4">
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
              <Bell className="w-8 h-8 text-blue-600" />
            </div>
          </div>
          <DialogTitle className="text-center text-xl">
            Bật thông báo?
          </DialogTitle>
          <DialogDescription className="text-center">
            Cho phép nhận thông báo để cập nhật ngay khi có:
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-3 py-4">
          <div className="flex items-start gap-3">
            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
              <span className="text-green-600 text-sm">✓</span>
            </div>
            <div>
              <p className="font-medium text-sm">Task được giao mới</p>
              <p className="text-xs text-gray-500">Nhận thông báo khi được giao nhiệm vụ</p>
            </div>
          </div>

          <div className="flex items-start gap-3">
            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
              <span className="text-green-600 text-sm">✓</span>
            </div>
            <div>
              <p className="font-medium text-sm">Bình luận và mention</p>
              <p className="text-xs text-gray-500">Được thông báo khi có người nhắc đến bạn</p>
            </div>
          </div>

          <div className="flex items-start gap-3">
            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
              <span className="text-green-600 text-sm">✓</span>
            </div>
            <div>
              <p className="font-medium text-sm">Cảnh báo deadline</p>
              <p className="text-xs text-gray-500">Nhắc nhở trước khi hết hạn nộp</p>
            </div>
          </div>

          <div className="flex items-start gap-3">
            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
              <span className="text-green-600 text-sm">✓</span>
            </div>
            <div>
              <p className="font-medium text-sm">Cập nhật dự án</p>
              <p className="text-xs text-gray-500">Thông báo về các thay đổi trong dự án</p>
            </div>
          </div>
        </div>

        <DialogFooter className="flex-col sm:flex-row gap-2">
          <Button
            variant="outline"
            onClick={handleSkip}
            disabled={isRequesting}
            className="w-full sm:w-auto"
          >
            <BellOff className="w-4 h-4 mr-2" />
            Để sau
          </Button>
          <Button
            onClick={handleAllow}
            disabled={isRequesting}
            className="w-full sm:w-auto"
          >
            <Bell className="w-4 h-4 mr-2" />
            {isRequesting ? 'Đang xử lý...' : 'Cho phép'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
