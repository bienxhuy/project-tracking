import { useEffect, useState } from "react"
import { fetchTempProjects } from "@/services/project.service"
import { fetchAllYears } from "@/services/semester.service"

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { LayoutDashboard, FolderKanban, Filter } from "lucide-react"
import { Button } from "@/components/ui/button";

import { Project } from "@/types/project.type";
import { ProjectCard } from "@/components/ProjectCard";


const alphabetSortOptions = {
  az: 'A-Z',
  za: 'Z-A',
};

const dateSortOptions = {
  newest: 'Mới nhất',
  oldest: 'Cũ nhất',
};


export const StudentDashboard = () => {
  // Current displayed projects
  const [projects, setProjects] = useState<Project[]>([]);
  // Available years for filtering
  const [years, setYears] = useState<number[]>([]);
  // Alphabetical sort state
  const [alphabetSort, setAlphabetSort] = useState<string>('az');
  // Date sort state
  const [dateSort, setDateSort] = useState<string>('newest');

  // Fetch data on component mount
  useEffect(() => {
    const currentBatchProject = fetchTempProjects();
    setProjects(currentBatchProject);

    const availableYears = fetchAllYears();
    setYears(availableYears);
  }, [])

  // Sort displaying projects whenever sort state changes
  const alphabeticallySortProjects = () => setProjects(projects.sort((a, b) => {
    if (alphabetSort === 'az') {
      return a.title.localeCompare(b.title);
    } else {
      return b.title.localeCompare(a.title);
    }
  }));

  const dateSortProjects = () => setProjects(projects.sort((a, b) => {
    if (dateSort === 'newest') {
      return b.startDate.getTime() - a.startDate.getTime();
    } else {
      return a.startDate.getTime() - b.startDate.getTime();
    }
  }));

  // Fetch projects based on filters
  const fetchProjectsBasedOnFilter = () => {
    // TODO: Implement API call to fetch projects based on selected filters
  }

  return (
    <>
      <div className="py-10 px-5 md:px-15 lg:px-32 bg-amber">

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

          {/**Displaying projects functional block*/}
          <div className="lg:flex lg:justify-between">

            {/* FILTER BAR */}
            <div className="flex flex-col md:flex-row md:items-center gap-2 md:gap-4 mb-2">

              {/* Search */}
              <div className="w-40">
                <Input
                  placeholder="Tìm kiếm"
                  className="h-10"
                />
              </div>

              {/* Filter group */}
              <div className="flex gap-2">

                {/* Year Select */}
                <Select>
                  <SelectTrigger className="w-28 h-10">
                    <SelectValue placeholder="Năm học" />
                  </SelectTrigger>
                  <SelectContent>
                    {years.map((year) => (
                      <SelectItem key={year} value={year.toString()}>{year}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>

                {/* Semester Select */}
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

                {/* Batch Select */}
                <Select>
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
                  size="icon"
                  variant="default"
                  onClick={fetchProjectsBasedOnFilter}>
                  <Filter />
                </Button>

              </div>
            </div>

            {/*Sort group*/}
            <div className="flex gap-2">
              <Button
                className="hover:bg-gray-200 hover:text-black cursor-pointer"
                size="sm"
                variant="outline"
                onClick={
                  () => { setDateSort(dateSort === 'newest' ? 'oldest' : 'newest'); dateSortProjects(); }
                }>
                {dateSortOptions[dateSort.toString() as keyof typeof dateSortOptions]}
              </Button>
              <Button
                className="hover:bg-gray-200 hover:text-black cursor-pointer gap-0"
                size="sm"
                variant="outline"
                onClick={
                  () => { setAlphabetSort(alphabetSort === 'az' ? 'za' : 'az'); alphabeticallySortProjects(); }
                }>
                {alphabetSortOptions[alphabetSort.toString() as keyof typeof alphabetSortOptions]}
              </Button>
            </div>

          </div>

          {/* PROJECT GRID */}
          <div className="grid grid-cols-1 md:grid-cols-1 lg:grid-cols-2 gap-4 w-full mt-2">
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
      </div >

    </>
  )
}