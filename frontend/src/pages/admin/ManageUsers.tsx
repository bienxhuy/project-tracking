import { useState, useEffect } from "react";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
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
  UpdateUserDto,
  UserStatus
} from "@/types/user.type";
import { userService } from "@/services/user.service";
import { Users } from "lucide-react";

export function ManageUsers() {
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [filters, setFilters] = useState<UserFiltersType>({
    search: "",
    role: "ALL",
    status: "ALL"
  });
  const [loading, setLoading] = useState(true);
  
  // Dialog states
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [bulkImportDialogOpen, setBulkImportDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  const { addToast } = useToast();

  // Load users and stats
  const loadData = async () => {
    setLoading(true);
    try {
      const [usersData, statsData] = await Promise.all([
        userService.getUsers(),
        userService.getUserStats()
      ]);
      setUsers(usersData);
      setFilteredUsers(usersData);
      setStats(statsData);
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

  // Apply filters
  useEffect(() => {
    const applyFilters = async () => {
      const filtered = await userService.getUsers(filters);
      setFilteredUsers(filtered);
    };
    applyFilters();
  }, [filters]);

  // Handle create user
  const handleCreateUser = async (data: CreateUserDto) => {
    try {
      await userService.createUser(data);
      addToast({
        title: "Success",
        description: "User created successfully",
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

  const handleUpdateUser = async (id: string, data: UpdateUserDto) => {
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
      if (user.status === UserStatus.ACTIVE) {
        await userService.deactivateUser(user.id);
        addToast({
          title: "Success",
          description: "User deactivated successfully",
          variant: "success"
        });
      } else {
        await userService.activateUser(user.id);
        addToast({
          title: "Success",
          description: "User activated successfully",
          variant: "success"
        });
      }
      await loadData();
    } catch (error) {
      addToast({
        title: "Error",
        description: "Failed to update user status",
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
    
    try {
      await userService.deleteUser(selectedUser.id);
      addToast({
        title: "Success",
        description: "User deleted successfully",
        variant: "success"
      });
      setDeleteDialogOpen(false);
      setSelectedUser(null);
      await loadData();
    } catch (error) {
      addToast({
        title: "Error",
        description: "Failed to delete user",
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
      {/* Breadcrumb */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Home</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href="/admin">Admin</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>Manage Users</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

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
      <UserStatsCards stats={stats || { totalStudents: 0, totalInstructors: 0, totalActive: 0, totalInactive: 0 }} loading={loading} />

      {/* Filters */}
      <UserFilters
        filters={filters}
        onFiltersChange={setFilters}
        onCreateUser={() => setCreateDialogOpen(true)}
        onBulkImport={() => setBulkImportDialogOpen(true)}
      />

      {/* User Table */}
      <UserTable
        users={filteredUsers}
        loading={loading}
        onViewUser={handleViewUser}
        onEditUser={handleEditUser}
        onToggleStatus={handleToggleStatus}
        onDeleteUser={handleDeleteUser}
      />

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

