import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { projectService } from "@/services/project.service"
import { fetchAllYears } from "@/services/semester.service"
import { toast } from "sonner"
import { useAuth } from "@/contexts/AuthContext"

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Filter, Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"

import { Project } from "@/types/project.type"
import { ProjectCard } from "@/components/ProjectCard"
import { FolderKanban } from "lucide-react"

const alphabetSortOptions = {
  az: 'A-Z',
  za: 'Z-A',
}

const dateSortOptions = {
  newest: 'Mới nhất',
  oldest: 'Cũ nhất',
}

export const InstructorDashboard = () => {
  const navigate = useNavigate()
  const { user } = useAuth()
  
  // Current displayed projects
  const [projects, setProjects] = useState<Project[]>([])
  // Available years for filtering
  const [years, setYears] = useState<number[]>([])
  // Filter states
  const [selectedYear, setSelectedYear] = useState<string>("")
  const [selectedSemester, setSelectedSemester] = useState<string>("")
  const [selectedBatch, setSelectedBatch] = useState<string>("")
  const [searchQuery, setSearchQuery] = useState<string>("")
  // Alphabetical sort state
  const [alphabetSort, setAlphabetSort] = useState<string>('az')
  // Date sort state
  const [dateSort, setDateSort] = useState<string>('newest')
  // Loading and error states
  const [isLoading, setIsLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  // Delete confirmation dialog
  const [deleteDialog, setDeleteDialog] = useState<{ open: boolean; projectId: number | null }>({
    open: false,
    projectId: null,
  })

  // Fetch data on component mount
  useEffect(() => {
    loadProjects()

    const availableYears = fetchAllYears()
    setYears(availableYears)
  }, [])

  const loadProjects = async (filters?: { year?: number; semester?: number; batch?: string }) => {
    if (!user?.id) {
      const errorMessage = "Không tìm thấy thông tin người dùng";
      setError(errorMessage);
      toast.error(errorMessage);
      setIsLoading(false);
      return
    }
    
    try {
      setIsLoading(true);
      setError(null);
      const response = await projectService.getInstructorProjects(user.id, filters);
      
      if (response.status !== "success") {
        throw new Error('Không thể tải danh sách dự án');
      }
      
      setProjects(response.data);
    } catch (error) {
      const errorMessage = "Không thể tải danh sách dự án";
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  }

  // Sort displaying projects whenever sort state changes
  const alphabeticallySortProjects = () => {
    const sorted = [...projects].sort((a, b) => {
      if (alphabetSort === 'az') {
        return a.title.localeCompare(b.title)
      } else {
        return b.title.localeCompare(a.title)
      }
    })
    setProjects(sorted)
  }

  const dateSortProjects = () => {
    const sorted = [...projects].sort((a, b) => {
      if (dateSort === 'newest') {
        return b.startDate.getTime() - a.startDate.getTime()
      } else {
        return a.startDate.getTime() - b.startDate.getTime()
      }
    })
    setProjects(sorted)
  }

  // Fetch projects based on filters
  const fetchProjectsBasedOnFilter = async () => {
    const filters: { year?: number; semester?: number; batch?: string } = {};
    
    if (selectedYear) filters.year = parseInt(selectedYear);
    if (selectedSemester) filters.semester = parseInt(selectedSemester);
    if (selectedBatch) filters.batch = selectedBatch;
    
    await loadProjects(filters);
  }

  // Handle project deletion
  const handleDeleteProject = async () => {
    if (deleteDialog.projectId === null) return

    try {
      const response = await projectService.deleteProject(deleteDialog.projectId);
      
      if (response.status !== "success") {
        throw new Error('Xóa dự án thất bại');
      }
      
      toast.success("Xóa dự án thành công");
      setDeleteDialog({ open: false, projectId: null });
      await loadProjects();
    } catch (error) {
      toast.error("Xóa dự án thất bại");
    }
  }

  // Navigate to create project page
  const handleCreateProject = () => {
    navigate("/instructor/project/create")
  }

  // Navigate to update project page
  const handleUpdateProject = (projectId: number) => {
    navigate(`/instructor/project/edit/${projectId}`)
  }

  // Open delete confirmation dialog
  const openDeleteDialog = (projectId: number) => {
    setDeleteDialog({ open: true, projectId })
  }

  return (
    <>
      <div className="pt-5 pb-10 px-5 md:px-15 lg:px-32 bg-amber">
        {/* DASHBOARD CONTENT */}
        <div className="space-y-6 mb-10">
            {/* HEADER WITH CREATE BUTTON */}
            <div className="flex justify-end items-center">
              <Button onClick={handleCreateProject} className="gap-2">
                <Plus className="w-4 h-4" />
                Tạo dự án mới
              </Button>
            </div>

            {/* METRICS BLOCK */}
            <div className="flex flex-col gap-2 mb-10">
              <h3 className="text-xl font-bold text-foreground">Thống kê</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="p-6 rounded-lg border border-border bg-gradient-card">
                  <div className="flex items-center justify-between mb-2">
                    <p className="text-sm font-medium text-muted-foreground">Dự án đã tạo</p>
                    <FolderKanban className="w-4 h-4 text-primary" />
                  </div>
                  <p className="text-3xl font-bold text-foreground">{projects.length}</p>
                </div>
              </div>
            </div>

            {/* PROJECTS BLOCK */}
            <div className="flex flex-col rounded-lg gap-2">
              <h3 className="text-xl font-bold text-foreground mb-2">Dự án đã tạo</h3>

              {/**Displaying projects functional block*/}
              <div className="lg:flex lg:justify-between">
                {/* FILTER BAR */}
                <div className="flex flex-col md:flex-row md:items-center gap-2 md:gap-4 mb-2">
                  {/* Search */}
                  <div className="w-40">
                    <Input 
                      placeholder="Tìm kiếm" 
                      className="h-10" 
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                    />
                  </div>

                  {/* Filter group */}
                  <div className="flex gap-2">
                    {/* Year Select */}
                    <Select value={selectedYear} onValueChange={setSelectedYear}>
                      <SelectTrigger className="w-28 h-10">
                        <SelectValue placeholder="Năm học" />
                      </SelectTrigger>
                      <SelectContent>
                        {years.map((year) => (
                          <SelectItem key={year} value={year.toString()}>
                            {year}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                    {/* Semester Select */}
                    <Select value={selectedSemester} onValueChange={setSelectedSemester}>
                      <SelectTrigger className="w-24 h-10">
                        <SelectValue placeholder="Học kỳ" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="1">HK1</SelectItem>
                        <SelectItem value="2">HK2</SelectItem>
                        <SelectItem value="3">HK3</SelectItem>
                      </SelectContent>
                    </Select>

                    {/* Batch Select */}
                    <Select value={selectedBatch} onValueChange={setSelectedBatch}>
                      <SelectTrigger className="w-20 h-10">
                        <SelectValue placeholder="Đợt" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="1">Đợt 1</SelectItem>
                        <SelectItem value="2">Đợt 2</SelectItem>
                      </SelectContent>
                    </Select>

                    {/* Filter button */}
                    <Button
                      className="cursor-pointer"
                      size="icon"
                      variant="default"
                      onClick={fetchProjectsBasedOnFilter}
                    >
                      <Filter />
                    </Button>
                  </div>
                </div>

                {/*Sort group*/}
                <div className="flex gap-2">
                  <Button
                    className="cursor-pointer"
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setDateSort(dateSort === 'newest' ? 'oldest' : 'newest')
                      dateSortProjects()
                    }}
                  >
                    {dateSortOptions[dateSort.toString() as keyof typeof dateSortOptions]}
                  </Button>
                  <Button
                    className="cursor-pointer"
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setAlphabetSort(alphabetSort === 'az' ? 'za' : 'az')
                      alphabeticallySortProjects()
                    }}
                  >
                    {alphabetSortOptions[alphabetSort.toString() as keyof typeof alphabetSortOptions]}
                  </Button>
                </div>
              </div>

              {/* PROJECT GRID */}
              <div className="grid grid-cols-1 md:grid-cols-1 lg:grid-cols-2 gap-4 w-full mt-2">
                {isLoading ? (
                  <div className="col-span-full flex items-center justify-center py-16">
                    <p className="text-muted-foreground">Đang tải...</p>
                  </div>
                ) : error ? (
                  <div className="col-span-full flex flex-col items-center justify-center py-16 px-4">
                    <p className="text-destructive text-center mb-2">{error}</p>
                    <Button variant="outline" onClick={() => loadProjects()}>
                      Thử lại
                    </Button>
                  </div>
                ) : projects.filter(project => 
                    project.title.toLowerCase().includes(searchQuery.toLowerCase())
                  ).length === 0 ? (
                  <div className="col-span-full flex flex-col items-center justify-center py-16 px-4">
                    <FolderKanban className="w-16 h-16 text-foreground/40 mb-4" />
                    <h3 className="text-xl font-semibold text-muted-foreground mb-2">
                      {searchQuery ? "Không tìm thấy dự án" : "Không có dự án nào được tìm thấy"}
                    </h3>
                    {searchQuery && (
                      <p className="text-muted-foreground text-center max-w-md">
                        Không tìm thấy dự án nào phù hợp với "{searchQuery}"
                      </p>
                    )}
                  </div>
                ) : (
                  projects
                    .filter(project => 
                      project.title.toLowerCase().includes(searchQuery.toLowerCase())
                    )
                    .map((project) => (
                    <ProjectCard
                      key={project.id}
                      id={project.id}
                      title={project.title}
                      semester={project.semester}
                      year={project.year}
                      batch={project.batch}
                      progress={
                        project.totalTasks === 0 ? 
                        0 : Math.round((project.totalCompletedTasks / project.totalTasks) * 100)}
                      members={project.totalMembers}
                      milestones={project.totalMilestones}
                      completedMilestones={project.totalCompletedMilestones}
                      status={project.status}
                      isLocked={project.isLocked}
                      onUpdate={() => handleUpdateProject(project.id)}
                      onDelete={() => openDeleteDialog(project.id)}
                      showActions={true}
                    />
                  ))
                )}
              </div>
            </div>
          </div>
        </div>

      {/* DELETE CONFIRMATION DIALOG */}
      <Dialog open={deleteDialog.open} onOpenChange={(open) => setDeleteDialog({ open, projectId: null })}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Xác nhận xóa dự án</DialogTitle>
            <DialogDescription>
              Bạn có chắc chắn muốn xóa dự án này không? Hành động này không thể hoàn tác.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteDialog({ open: false, projectId: null })}>
              Hủy
            </Button>
            <Button variant="destructive" onClick={handleDeleteProject}>
              Xóa
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  )
}
