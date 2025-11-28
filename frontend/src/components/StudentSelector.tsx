import { useState, useEffect } from "react"
import { X, Search, UserPlus } from "lucide-react"
import { BaseUser } from "@/types/user.type"
import { searchStudents } from "@/services/user.service"

import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"

interface StudentSelectorProps {
  selectedStudents: BaseUser[]
  onStudentsChange: (students: BaseUser[]) => void
}

export const StudentSelector = ({ selectedStudents, onStudentsChange }: StudentSelectorProps) => {
  const [searchQuery, setSearchQuery] = useState("")
  const [searchResults, setSearchResults] = useState<BaseUser[]>([])
  const [showResults, setShowResults] = useState(false)

  useEffect(() => {
    if (searchQuery.trim()) {
      // Call the search service from user.service.ts
      const results = searchStudents(searchQuery)
      // Filter out already selected students
      const filteredResults = results.filter(
        (student) => !selectedStudents.find((s) => s.id === student.id)
      )
      setSearchResults(filteredResults)
      setShowResults(true)
    } else {
      setSearchResults([])
      setShowResults(false)
    }
  }, [searchQuery, selectedStudents])

  const addStudent = (student: BaseUser) => {
    onStudentsChange([...selectedStudents, student])
    setSearchQuery("")
    setShowResults(false)
  }

  const removeStudent = (studentId: number) => {
    onStudentsChange(selectedStudents.filter((s) => s.id !== studentId))
  }

  const getInitials = (name: string) => {
    const parts = name.split(" ")
    if (parts.length >= 2) {
      return parts[0][0] + parts[parts.length - 1][0]
    }
    return name.substring(0, 2)
  }

  return (
    <div className="space-y-4">
      {/* Search Input */}
      <div className="relative">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Tìm kiếm sinh viên theo tên, email hoặc ID..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onFocus={() => searchQuery && setShowResults(true)}
            className="pl-10"
          />
        </div>

        {/* Search Results Dropdown */}
        {showResults && searchResults.length > 0 && (
          <Card className="absolute z-10 w-full mt-2 max-h-64 overflow-y-auto">
            <CardContent className="p-2">
              {searchResults.map((student) => (
                <div
                  key={student.id}
                  className="flex items-center justify-between p-3 hover:bg-gray-100 rounded-md cursor-pointer transition-colors"
                  onClick={() => addStudent(student)}
                >
                  <div className="flex items-center gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarFallback className="text-xs">
                        {getInitials(student.full_name)}
                      </AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="text-sm font-medium">{student.full_name}</p>
                      <p className="text-xs text-muted-foreground">{student.email}</p>
                    </div>
                  </div>
                  <Button size="sm" variant="ghost">
                    <UserPlus className="h-4 w-4" />
                  </Button>
                </div>
              ))}
            </CardContent>
          </Card>
        )}
      </div>

      {/* Selected Students */}
      {selectedStudents.length > 0 && (
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <p className="text-sm font-medium">
              Sinh viên đã chọn ({selectedStudents.length})
            </p>
          </div>
          <div className="grid grid-cols-1 gap-2">
            {selectedStudents.map((student) => (
              <div
                key={student.id}
                className="flex items-center justify-between p-3 border rounded-md bg-card"
              >
                <div className="flex items-center gap-3">
                  <Avatar className="h-8 w-8">
                    <AvatarFallback className="text-xs">
                      {getInitials(student.full_name)}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">{student.full_name}</p>
                    <p className="text-xs text-muted-foreground">{student.email}</p>
                  </div>
                </div>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => removeStudent(student.id)}
                >
                  <X className="h-4 w-4" />
                </Button>
              </div>
            ))}
          </div>
        </div>
      )}

      {selectedStudents.length === 0 && (
        <div className="text-center py-8 text-muted-foreground">
          <UserPlus className="h-12 w-12 mx-auto mb-2 opacity-50" />
          <p className="text-xs">Sử dụng ô tìm kiếm để thêm sinh viên vào dự án</p>
        </div>
      )}
    </div>
  )
}
