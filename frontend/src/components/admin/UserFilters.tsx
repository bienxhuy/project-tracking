import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Search, Plus, Upload } from "lucide-react";
import { UserRole, UserStatus, UserFilters as UserFiltersType } from "@/types/user.type";

interface UserFiltersProps {
  filters: UserFiltersType;
  onFiltersChange: (filters: UserFiltersType) => void;
  onCreateUser: () => void;
  onBulkImport: () => void;
}

export function UserFilters({
  filters,
  onFiltersChange,
  onCreateUser,
  onBulkImport
}: UserFiltersProps) {
  const [searchValue, setSearchValue] = useState(filters.search || "");

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
    // Debounce search
    setTimeout(() => {
      onFiltersChange({ ...filters, search: value });
    }, 300);
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        {/* Search Bar */}
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search by name, email, or ID..."
            value={searchValue}
            onChange={(e) => handleSearchChange(e.target.value)}
            className="pl-9"
          />
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-2">
          {/* Role Filter */}
          <Select
            value={filters.role || "ALL"}
            onValueChange={(value) => 
              onFiltersChange({ ...filters, role: value as UserRole | "ALL" })
            }
          >
            <SelectTrigger className="w-[150px]">
              <SelectValue placeholder="Role" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Roles</SelectItem>
              <SelectItem value={UserRole.STUDENT}>Student</SelectItem>
              <SelectItem value={UserRole.INSTRUCTOR}>Instructor</SelectItem>
            </SelectContent>
          </Select>

          {/* Status Filter */}
          <Select
            value={filters.status || "ALL"}
            onValueChange={(value) => 
              onFiltersChange({ ...filters, status: value as UserStatus | "ALL" })
            }
          >
            <SelectTrigger className="w-[150px]">
              <SelectValue placeholder="Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Status</SelectItem>
              <SelectItem value={UserStatus.ACTIVE}>Active</SelectItem>
              <SelectItem value={UserStatus.INACTIVE}>Inactive</SelectItem>
            </SelectContent>
          </Select>

          {/* Action Buttons */}
          <Button onClick={onBulkImport} variant="outline">
            <Upload className="mr-2 h-4 w-4" />
            Bulk Import
          </Button>
          <Button onClick={onCreateUser}>
            <Plus className="mr-2 h-4 w-4" />
            Create User
          </Button>
        </div>
      </div>
    </div>
  );
}

