"use client"

import { useEffect, useState } from "react"
import { useParams, useRouter } from "next/navigation"
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
import Link from "next/link"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

interface ClassDetails {
  id: number
  name: string
  subjectName: string
  academicYearName: string
  creationDate: string
}

interface Student {
  id: number
  firstName: string
  lastName: string
  email: string
}

interface Material {
  id: number
  title: string
  subjectName: string
  creationDate: string
}

interface Quiz {
  id: number
  title: string
  creationDate: string
  hasPendingAnswers?: boolean
}

interface Assessment {
  id: number
  title: string
  grade: number
  studentName: string
  date: string
}

export default function ClassDetailsPage() {
  const { id } = useParams()
  const [classDetails, setClassDetails] = useState<ClassDetails | null>(null)
  const [students, setStudents] = useState<Student[]>([])
  const [materials, setMaterials] = useState<Material[]>([])
  const [quizzes, setQuizzes] = useState<Quiz[]>([])
  const [assessments, setAssessments] = useState<Assessment[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isAddStudentDialogOpen, setIsAddStudentDialogOpen] = useState(false)
  const [studentEmail, setStudentEmail] = useState("")
  const { user, isAuthenticated } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  const isTeacher = user?.role === "TEACHER"

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login")
      return
    }

    const fetchClassDetails = async () => {
      try {
        const response = await api.get(`/classes/${id}`)
        setClassDetails(response.data)
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar os detalhes da turma.",
        })
        router.push("/classes")
      }
    }

    const fetchStudents = async () => {
      try {
        const response = await api.get(`/classes/${id}/students`)
        setStudents(response.data)
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar os alunos da turma.",
        })
      }
    }

    const fetchMaterials = async () => {
      try {
        const response = await api.get(`/materials?classeId=${id}`)
        setMaterials(response.data)
      } catch (error) {
        console.error("Failed to fetch materials:", error)
      }
    }

    const fetchQuizzes = async () => {
      try {
        const response = await api.get(`/quizzes?classeId=${id}`)
        setQuizzes(response.data)
      } catch (error) {
        console.error("Failed to fetch quizzes:", error)
      }
    }

    const fetchAssessments = async () => {
      try {
        const response = await api.get(`/assessments/classe/${id}`)
        setAssessments(response.data)
      } catch (error) {
        console.error("Failed to fetch assessments:", error)
      }
    }

    Promise.all([fetchClassDetails(), fetchStudents(), fetchMaterials(), fetchQuizzes(), fetchAssessments()]).finally(
      () => {
        setIsLoading(false)
      },
    )
  }, [id, isAuthenticated, router, toast])

  const handleAddStudent = async () => {
    if (!studentEmail) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Por favor, introduza o email do aluno.",
      })
      return
    }

    try {
      const response = await api.get(`/students/search?email=${studentEmail}`)
      const student = response.data

      if (!student) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Aluno não encontrado.",
        })
        return
      }

      await api.post(`/classes/${id}/students`, {
        studentIds: [student.id],
      })

      setStudents([...students, student])
      setIsAddStudentDialogOpen(false)
      setStudentEmail("")

      toast({
        title: "Aluno adicionado",
        description: "O aluno foi adicionado à turma com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível adicionar o aluno à turma.",
      })
    }
  }

  const handleRemoveStudent = async (studentId: number) => {
    try {
      await api.delete(`/classes/${id}/students`, {
        data: { studentIds: [studentId] },
      })

      setStudents(students.filter((student) => student.id !== studentId))

      toast({
        title: "Aluno removido",
        description: "O aluno foi removido da turma com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível remover o aluno da turma.",
      })
    }
  }

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar detalhes da turma...</p>
          </div>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-[#1E40AF]">{classDetails?.name}</h1>
          <Button variant="outline" asChild>
            <Link href="/classes">Voltar às Turmas</Link>
          </Button>
        </div>

        <Card className="mb-6">
          <CardHeader>
            <CardTitle className="text-lg">Detalhes da Turma</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <p className="text-sm font-medium">Disciplina</p>
                <p className="text-gray-500">{classDetails?.subjectName}</p>
              </div>
              <div>
                <p className="text-sm font-medium">Ano Letivo</p>
                <p className="text-gray-500">{classDetails?.academicYearName}</p>
              </div>
              <div>
                <p className="text-sm font-medium">Data de Criação</p>
                <p className="text-gray-500">
                  {classDetails?.creationDate && new Date(classDetails.creationDate).toLocaleDateString("pt-PT")}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Tabs defaultValue="students">
          <TabsList className="mb-4">
            <TabsTrigger value="students">Alunos</TabsTrigger>
            <TabsTrigger value="materials">Materiais</TabsTrigger>
            <TabsTrigger value="quizzes">Quizzes</TabsTrigger>
            <TabsTrigger value="assessments">Avaliações</TabsTrigger>
          </TabsList>

          <TabsContent value="students">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">Alunos</CardTitle>
                {isTeacher && (
                  <Dialog open={isAddStudentDialogOpen} onOpenChange={setIsAddStudentDialogOpen}>
                    <DialogTrigger asChild>
                      <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">Adicionar Aluno</Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Adicionar Aluno à Turma</DialogTitle>
                        <DialogDescription>
                          Introduza o email do aluno que pretende adicionar à turma.
                        </DialogDescription>
                      </DialogHeader>
                      <div className="space-y-4 py-4">
                        <div className="space-y-2">
                          <Label htmlFor="email">Email do Aluno</Label>
                          <Input
                            id="email"
                            type="email"
                            value={studentEmail}
                            onChange={(e) => setStudentEmail(e.target.value)}
                            placeholder="aluno@exemplo.com"
                          />
                        </div>
                      </div>
                      <DialogFooter>
                        <Button variant="outline" onClick={() => setIsAddStudentDialogOpen(false)}>
                          Cancelar
                        </Button>
                        <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={handleAddStudent}>
                          Adicionar
                        </Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                )}
              </CardHeader>
              <CardContent>
                {students.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Nome</TableHead>
                        <TableHead>Email</TableHead>
                        {isTeacher && <TableHead className="text-right">Ações</TableHead>}
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {students.map((student) => (
                        <TableRow key={student.id}>
                          <TableCell className="font-medium">
                            {student.firstName} {student.lastName}
                          </TableCell>
                          <TableCell>{student.email}</TableCell>
                          {isTeacher && (
                            <TableCell className="text-right">
                              <Button variant="destructive" size="sm" onClick={() => handleRemoveStudent(student.id)}>
                                Remover
                              </Button>
                            </TableCell>
                          )}
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <div className="text-center py-4 text-gray-500">Não existem alunos nesta turma.</div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="materials">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">Materiais</CardTitle>
                {isTeacher && (
                  <Button asChild className="bg-[#1E40AF] hover:bg-[#1E3A8A]">
                    <Link href={`/materials?classeId=${id}`}>Criar Material</Link>
                  </Button>
                )}
              </CardHeader>
              <CardContent>
                {materials.length > 0 ? (
                  <div className="grid gap-4 md:grid-cols-2">
                    {materials.map((material) => (
                      <Card key={material.id}>
                        <CardContent className="p-4">
                          <div className="font-medium">{material.title}</div>
                          <div className="text-sm text-gray-500">{material.subjectName}</div>
                          <div className="text-sm text-gray-500">
                            {new Date(material.creationDate).toLocaleDateString("pt-PT")}
                          </div>
                          <Button asChild variant="outline" className="mt-2 w-full">
                            <Link href={`/materials/${material.id}`}>Ver Material</Link>
                          </Button>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-4 text-gray-500">Não existem materiais para esta turma.</div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="quizzes">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">Quizzes</CardTitle>
                {isTeacher && (
                  <Button asChild className="bg-[#1E40AF] hover:bg-[#1E3A8A]">
                    <Link href={`/quizzes?classeId=${id}`}>Criar Quiz</Link>
                  </Button>
                )}
              </CardHeader>
              <CardContent>
                {quizzes.length > 0 ? (
                  <div className="grid gap-4 md:grid-cols-2">
                    {quizzes.map((quiz) => (
                      <Card key={quiz.id}>
                        <CardContent className="p-4">
                          <div className="font-medium">{quiz.title}</div>
                          <div className="text-sm text-gray-500">
                            {new Date(quiz.creationDate).toLocaleDateString("pt-PT")}
                          </div>
                          {!isTeacher && quiz.hasPendingAnswers !== undefined && (
                            <div className={`text-sm ${quiz.hasPendingAnswers ? "text-amber-500" : "text-green-500"}`}>
                              {quiz.hasPendingAnswers ? "Pendente" : "Concluído"}
                            </div>
                          )}
                          <Button asChild variant="outline" className="mt-2 w-full">
                            <Link href={`/quizzes/${quiz.id}`}>{isTeacher ? "Ver Respostas" : "Responder"}</Link>
                          </Button>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-4 text-gray-500">Não existem quizzes para esta turma.</div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="assessments">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg">Avaliações</CardTitle>
                {isTeacher && (
                  <Button asChild className="bg-[#1E40AF] hover:bg-[#1E3A8A]">
                    <Link href={`/assessments?classeId=${id}`}>Criar Avaliação</Link>
                  </Button>
                )}
              </CardHeader>
              <CardContent>
                {assessments.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Título</TableHead>
                        <TableHead>Aluno</TableHead>
                        <TableHead>Nota</TableHead>
                        <TableHead>Data</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {assessments.map((assessment) => (
                        <TableRow key={assessment.id}>
                          <TableCell className="font-medium">{assessment.title}</TableCell>
                          <TableCell>{assessment.studentName}</TableCell>
                          <TableCell>{assessment.grade}</TableCell>
                          <TableCell>{new Date(assessment.date).toLocaleDateString("pt-PT")}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <div className="text-center py-4 text-gray-500">Não existem avaliações para esta turma.</div>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </AppLayout>
  )
}
