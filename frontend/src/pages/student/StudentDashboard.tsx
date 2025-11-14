import { useEffect, useState } from "react"
import { fetchTempProjects } from "@/services/project.service"

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { LayoutDashboard, FolderKanban } from "lucide-react"

import { Project } from "@/types/project.type";
import { ProjectCard } from "@/components/ProjectCard";

export const StudentDashboard = () => {
  const [projects, setProjects] = useState<Project[]>([]);

  useEffect(() => {
    const currentBatchProject = fetchTempProjects();
    setProjects(currentBatchProject);
  }, [])

  return (
    <>
      <div className="py-10 px-0 md:px-20 lg:px-32 bg-amber">

        <Tabs defaultValue="dashboard" className="space-y-6 mb-10">
          <TabsList className="bg-secondary">
            <TabsTrigger value="dashboard" className="gap-2">
              <LayoutDashboard className="w-4 h-4" />
              Tổng quan
            </TabsTrigger>
            <TabsTrigger value="projects" className="gap-2">
              <FolderKanban className="w-4 h-4" />
              Gấp
            </TabsTrigger>
          </TabsList>
        </Tabs>

        {/* METRICS BLOCK */}
        <div className="flex flex-col gap-2 mb-10">
          <h3 className="text-xl font-bold text-foreground">
            Thống kê
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-6 rounded-lg border border-border bg-gradient-card">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium text-muted-foreground">Dự án đang hoạt động</p>
                <FolderKanban className="w-4 h-4 text-primary" />
              </div>
              <p className="text-3xl font-bold text-foreground">{projects.length}</p>
            </div>
            <div className="p-6 rounded-lg border border-border bg-gradient-card">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm font-medium text-muted-foreground">Nhiệm vụ chờ xử lý</p>
                <FolderKanban className="w-4 h-4 text-warning" />
              </div>
              <p className="text-3xl font-bold text-foreground">12</p>
              <p className="text-xs text-muted-foreground mt-1">8 hạn tuần này</p>
            </div>
          </div>
        </div>

        {/* PROJECTS BLOCK */}
        <div className="flex flex-col rounded-lg gap-2">
          <h3 className="text-xl font-bold text-foreground">Dự án hiện tại</h3>

          {/* FILTER BAR */}
          <div className="flex flex-col md:flex-row md:items-center gap-3 md:gap-4">

            {/* Search */}
            <div className="w-40">
              <Input
                placeholder="Tìm kiếm"
                className="h-10"
              />
            </div>

            {/* Filter group */}
            <div className="flex gap-2">

              <Select>
                <SelectTrigger className="w-28 h-10">
                  <SelectValue placeholder="Năm học" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="2024">2024</SelectItem>
                  <SelectItem value="2025">2025</SelectItem>
                </SelectContent>
              </Select>

              <Select>
                <SelectTrigger className="w-24 h-10">
                  <SelectValue placeholder="Học kỳ" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="1">HK1</SelectItem>
                  <SelectItem value="2">HK2</SelectItem>
                  <SelectItem value="3">HK3</SelectItem>
                </SelectContent>
              </Select>

              <Select>
                <SelectTrigger className="w-20 h-10">
                  <SelectValue placeholder="Đợt" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="1">Đợt 1</SelectItem>
                  <SelectItem value="2">Đợt 2</SelectItem>
                </SelectContent>
              </Select>

            </div>
          </div>

          {/* PROJECT GRID */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 w-full mt-2">
            {projects.map((project) => (
              <ProjectCard
                key={project.id}
                id={project.id}
                title={project.title}
                semester={project.semester}
                year={project.year}
                batch={project.batch}
                progress={project.completionPercentage}
                members={project.memberCount}
                milestones={project.milestoneCount}
                completedMilestones={1}
                status={project.status}
              />
            ))}
          </div>
        </div>
      </div>

    </>
  )
}