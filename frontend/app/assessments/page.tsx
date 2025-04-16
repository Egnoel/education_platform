"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { useAuth } from "@/lib/context/AuthContext"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useToast } from "@/hooks/use-toast"
import { api } from "@/lib/api"
import AppLayout from "@/components/AppLayout"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

interface Assessment {
  id: number
  title: string
  grade: number
  classeName: string
  studentName: string
  date: string
}

interface Class {
  id: number
  name: string
}

interface Student {
  id: number
  firstName: string
  lastName: string
}

export default function AssessmentsPage() {
  const searchParams = useSearchParams()
  const classeId = searchParams.get("classeId")

  const [assessments, setAssessments] = useState<Assessment[]>([])
  const [classes, setClasses] = useState<Class[]>([])
  const [students, setStudents] = useState<Student[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [title, setTitle] = useState("")
  const [grade, setGrade] = useState("")
  const [selectedClass, setSelectedClass] = useState(classeId || "")
  const [selectedStudent, setSelectedStudent] = useState("")
  const { user, isAuthenticated } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  const isTeacher = user?.role === "TEACHER"

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login")
      return
    }

    const fetchAssessments = async () => {
      try {
        let url
        if (isTeacher) {
          if (classeId) {
            url = `/assessments/classe/${classeId}`
          } else {
            // Fetch all assessments for teacher (might need a different endpoint)
            url = "/assessments"
          }
        } else {
          url = `/assessments/student/${user?.id}`
        }

        const response = await api.get(url)
        setAssessments(response.data)
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar as avaliações.",
        })
      } finally {
        setIsLoading(false)
      }
    }

    const fetchClassesAndStudents = async () => {
      if (isTeacher) {
        try {
          const classesResponse = await api.get("/classes")
          setClasses(classesResponse.data)

          if (classeId) {
            const studentsResponse = await api.get(`/classes/${classeId}/students`)
            setStudents(studentsResponse.data)
          }
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Erro",
            description: "Não foi possível carregar os dados para criar avaliações.",
          })
        }
      }
    }

    fetchAssessments()
    fetchClassesAndStudents()
  }, [classeId, isAuthenticated, isTeacher, router, toast, user])

  useEffect(() => {
    if (isTeacher && selectedClass) {
      const fetchStudentsForClass = async () => {
        try {
          const response = await api.get(`/classes/${selectedClass}/students`)
          setStudents(response.data)
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Erro",
            description: "Não foi possível carregar os alunos da turma.",
          })
        }
      }

      fetchStudentsForClass()
    }
  }, [isTeacher, selectedClass, toast])

  const handleCreateAssessment = async () => {
    if (!title || !grade || !selectedClass || !selectedStudent) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Por favor, preencha todos os campos.",
      })
      return
    }

    const gradeValue = Number.parseFloat(grade)
    if (isNaN(gradeValue) || gradeValue < 0 || gradeValue > 20) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "A nota deve ser um número entre 0 e 20.",
      })
      return
    }

    try {
      const response = await api.post("/assessments", {
        title,
        grade: gradeValue,
        date: new Date().toISOString(),
        studentId: Number.parseInt(selectedStudent),
        classeId: Number.parseInt(selectedClass),
      })

      setAssessments([...assessments, response.data])
      setIsDialogOpen(false)
      setTitle("")
      setGrade("")
      setSelectedClass(classeId || "")
      setSelectedStudent("")

      toast({
        title: "Avaliação criada",
        description: "A avaliação foi criada com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível criar a avaliação.",
      })
    }
  }

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar avaliações...</p>
          </div>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-[#1E40AF]">Avaliações</h1>
          {isTeacher && (
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">Criar Avaliação</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Criar Nova Avaliação</DialogTitle>
                  <DialogDescription>Preencha os detalhes para criar uma nova avaliação.</DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <Label htmlFor="title">Título</Label>
                    <Input
                      id="title"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      placeholder="Ex: Teste Final - 1º Período"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="grade">Nota (0-20)</Label>
                    <Input
                      id="grade"
                      type="number"
                      min="0"
                      max="20"
                      step="0.1"
                      value={grade}
                      onChange={(e) => setGrade(e.target.value)}
                      placeholder="Ex: 15.5"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="class">Turma</Label>
                    <Select value={selectedClass} onValueChange={setSelectedClass}>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione uma turma" />
                      </SelectTrigger>
                      <SelectContent>
                        {classes.map((classe) => (
                          <SelectItem key={classe.id} value={classe.id.toString()}>
                            {classe.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="student">Aluno</Label>
                    <Select
                      value={selectedStudent}
                      onValueChange={setSelectedStudent}
                      disabled={!selectedClass || students.length === 0}
                    >
                      <SelectTrigger>
                        <SelectValue
                          placeholder={
                            !selectedClass
                              ? "Selecione primeiro uma turma"
                              : students.length === 0
                                ? "Não existem alunos nesta turma"
                                : "Selecione um aluno"
                          }
                        />
                      </SelectTrigger>
                      <SelectContent>
                        {students.map((student) => (
                          <SelectItem key={student.id} value={student.id.toString()}>
                            {student.firstName} {student.lastName}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancelar
                  </Button>
                  <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={handleCreateAssessment}>
                    Criar
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">{isTeacher ? "Avaliações Atribuídas" : "As Suas Avaliações"}</CardTitle>
          </CardHeader>
          <CardContent>
            {assessments.length > 0 ? (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Título</TableHead>
                    {isTeacher && <TableHead>Aluno</TableHead>}
                    <TableHead>Turma</TableHead>
                    <TableHead>Nota</TableHead>
                    <TableHead>Data</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {assessments.map((assessment) => (
                    <TableRow key={assessment.id}>
                      <TableCell className="font-medium">{assessment.title}</TableCell>
                      {isTeacher && <TableCell>{assessment.studentName}</TableCell>}
                      <TableCell>{assessment.classeName}</TableCell>
                      <TableCell>{assessment.grade}</TableCell>
                      <TableCell>{new Date(assessment.date).toLocaleDateString("pt-PT")}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <div className="text-center py-4 text-gray-500">Não existem avaliações disponíveis.</div>
            )}
          </CardContent>
        </Card>
      </div>
    </AppLayout>
  )
}
