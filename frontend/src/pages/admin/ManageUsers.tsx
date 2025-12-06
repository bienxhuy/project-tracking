import { useState, useEffect } from "react";
import { useToast } from "@/components/ui/toast";
import { UserStatsCards } from "@/components/admin/UserStatsCards";
import { UserFilters } from "@/components/admin/UserFilters";
import { UserTable } from "@/components/admin/UserTable";
import { CreateUserDialog } from "@/components/admin/CreateUserDialog";
import { EditUserDialog } from "@/components/admin/EditUserDialog";
import { ViewUserDialog } from "@/components/admin/ViewUserDialog";
import { BulkImportDialog } from "@/components/admin/BulkImportDialog";
import { DeleteUserDialog } from "@/components/admin/DeleteUserDialog";
import { 
  User, 
  UserFilters as UserFiltersType, 
  UserStats,
  CreateUserDto,
  UpdateUserDto
} from "@/types/user.type";
import { UserStatus, UserRole } from "@/types/util.type";
import { userService } from "@/services/user.service"; // Dummy data service
import { Users } from "lucide-react";

export function ManageUsers() {
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [filters, setFilters] = useState<UserFiltersType>({
    search: "",
    role: "ALL"
  });
  const [loading, setLoading] = useState(true);
  const [isFiltering, setIsFiltering] = useState(false);
  
  // Dialog states
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [bulkImportDialogOpen, setBulkImportDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  const { addToast } = useToast();

  // Load users and calculate stats
  const loadData = async () => {
    setLoading(true);
    try {
      // Fetch all users from backend (no filters for stats calculation)
      const usersData = await userService.getUsers();
      setFilteredUsers(usersData);
      
      // Calculate stats from all users data
      const calculatedStats = userService.calculateStats(usersData);
      setStats(calculatedStats);
    } catch (error) {
      console.error("Failed to load data:", error);
      addToast({
        title: "Error",
        description: "Failed to load user data",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Apply filters from backend (without showing loading spinner)
  useEffect(() => {
    // Skip filter on initial load (handled by loadData)
    if (loading) return;
    
    const applyFilters = async () => {
      setIsFiltering(true);
      try {
        const filtered = await userService.getUsers(filters);
        setFilteredUsers(filtered);
      } catch (error) {
        console.error("Failed to apply filters:", error);
        addToast({
          title: "Error",
          description: "Failed to apply filters",
          variant: "destructive"
        });
      } finally {
        setIsFiltering(false);
      }
    };
    applyFilters();
  }, [filters, loading]);

  // Handle create user
  const handleCreateUser = async (data: CreateUserDto): Promise<void> => {
    try {
      await userService.createUser(data);
      addToast({
        title: "User Created Successfully! 📧",
        description: `Verification email sent to ${data.email}`,
        variant: "success"
      });
      await loadData();
    } catch (error) {
      addToast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to create user",
        variant: "destructive"
      });
      throw error;
    }
  };

  // Handle edit user
  const handleEditUser = (user: User) => {
    setSelectedUser(user);
    setEditDialogOpen(true);
  };

  const handleUpdateUser = async (id: number, data: UpdateUserDto) => {
    try {
      await userService.updateUser(id, data);
      addToast({
        title: "Success",
        description: "User updated successfully",
        variant: "success"
      });
      await loadData();
    } catch (error) {
      addToast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update user",
        variant: "destructive"
      });
      throw error;
    }
  };

  // Handle view user
  const handleViewUser = (user: User) => {
    setSelectedUser(user);
    setViewDialogOpen(true);
  };

  // Handle toggle status
  const handleToggleStatus = async (user: User) => {
    try {
      let updatedUser: User;
      let successMessage: string;

      if (user.accountStatus === UserStatus.ACTIVE) {
        updatedUser = await userService.deactivateUser(user.id);
        successMessage = "User deactivated successfully";
      } else if (user.accountStatus === UserStatus.INACTIVE) {
        updatedUser = await userService.activateUser(user.id);
        successMessage = "User activated successfully";
      } else if (user.accountStatus === UserStatus.VERIFYING) {
        updatedUser = await userService.activateUser(user.id);
        successMessage = "User activated successfully";
      } else {
        throw new Error("Unknown user status");
      }

      // Update ONLY the changed user in local state
      const updatedUsers = filteredUsers.map(u =>
        u.id === updatedUser.id ? updatedUser : u
      );
      setFilteredUsers(updatedUsers);

      // Recalculate stats from updated users (removed accountStatus logic)

      addToast({
        title: "Success",
        description: successMessage,
        variant: "success"
      });
    } catch (error) {
      addToast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update user status",
        variant: "destructive"
      });
    }
  };

  // Handle delete user
  const handleDeleteUser = (user: User) => {
    setSelectedUser(user);
    setDeleteDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedUser) return;
    
    const deletedUser = selectedUser;
    
    try {
      // 1. Xóa NGAY khỏi UI (Optimistic)
      setFilteredUsers(prevUsers => 
        prevUsers.filter(u => u.id !== deletedUser.id)
      );
      
      // 2. Update stats ngay
      if (stats) {
        setStats(prevStats => {
          if (!prevStats) return prevStats;
          const newStats = { ...prevStats };
          newStats.totalUsers = Math.max(0, newStats.totalUsers - 1);
          
          if (deletedUser.role === UserRole.ADMIN) {
            newStats.totalAdmins = Math.max(0, newStats.totalAdmins - 1);
          } else if (deletedUser.role === UserRole.INSTRUCTOR) {
            newStats.totalInstructors = Math.max(0, newStats.totalInstructors - 1);
          } else if (deletedUser.role === UserRole.STUDENT) {
            newStats.totalStudents = Math.max(0, newStats.totalStudents - 1);
          }
          
          return newStats;
        });
      }
      
      // 3. Close dialog ngay
      setDeleteDialogOpen(false);
      setSelectedUser(null);
      
      // 4. Gọi API ở background
      await userService.deleteUser(deletedUser.id);
      
      addToast({
        title: "Success",
        description: `User "${deletedUser.displayName}" deleted successfully`,
        variant: "success"
      });
    } catch (error) {
      // Nếu API fail → restore user lại
      await loadData();
      
      addToast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to delete user",
        variant: "destructive"
      });
    }
  };

  // Handle bulk import
  const handleBulkImport = async (usersData: CreateUserDto[]) => {
    const result = await userService.bulkCreateUsers({ users: usersData });
    await loadData();
    return result;
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <div className="flex items-center gap-2 mb-2">
          <Users className="h-8 w-8 text-primary" />
          <h1 className="text-3xl font-bold">Manage User Accounts</h1>
        </div>
        <p className="text-muted-foreground">
          Create, view, update, and manage student and instructor accounts.
        </p>
      </div>

      {/* Stats Cards */}
      <UserStatsCards 
        stats={stats || { 
          totalUsers: 0, 
          totalAdmins: 0, 
          totalInstructors: 0, 
          totalStudents: 0, 
          totalInactive: 0
        }} 
        loading={loading} 
      />

      {/* Filters */}
      <UserFilters
        filters={filters}
        onFiltersChange={setFilters}
        onCreateUser={() => setCreateDialogOpen(true)}
        onBulkImport={() => setBulkImportDialogOpen(true)}
      />

      {/* User Table */}
      <div className={isFiltering ? "opacity-75 transition-opacity duration-200" : "opacity-100 transition-opacity duration-200"}>
        <UserTable
          users={filteredUsers}
          loading={loading}
          onViewUser={handleViewUser}
          onEditUser={handleEditUser}
          onToggleStatus={handleToggleStatus}
          onDeleteUser={handleDeleteUser}
        />
      </div>

      {/* Dialogs */}
      <CreateUserDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
        onSubmit={handleCreateUser}
      />

      <EditUserDialog
        open={editDialogOpen}
        user={selectedUser}
        onOpenChange={setEditDialogOpen}
        onSubmit={handleUpdateUser}
      />

      <ViewUserDialog
        open={viewDialogOpen}
        user={selectedUser}
        onOpenChange={setViewDialogOpen}
      />

      <BulkImportDialog
        open={bulkImportDialogOpen}
        onOpenChange={setBulkImportDialogOpen}
        onSubmit={handleBulkImport}
      />

      <DeleteUserDialog
        open={deleteDialogOpen}
        user={selectedUser}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleConfirmDelete}
      />
    </div>
  );
}


