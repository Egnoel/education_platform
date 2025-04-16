"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/lib/context/AuthContext"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useToast } from "@/hooks/use-toast"
import { api } from "@/lib/api"
import AppLayout from "@/components/AppLayout"
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
import Link from "next/link"

interface Class {
  id: number
  name: string
  subjectName: string
  academicYearName: string
  creationDate: string
}

interface Subject {
  id: number
  name: string
}

interface AcademicYear {
  id: number
  name: string
}

export default function ClassesPage() {
  const [classes, setClasses] = useState<Class[]>([])
  const [subjects, setSubjects] = useState<Subject[]>([])
  const [academicYears, setAcademicYears] = useState<AcademicYear[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [newClassName, setNewClassName] = useState("")
  const [selectedSubject, setSelectedSubject] = useState("")
  const [selectedAcademicYear, setSelectedAcademicYear] = useState("")
  const { user, isAuthenticated } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  const isTeacher = user?.role === "TEACHER"

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login")
      return
    }

    const fetchClasses = async () => {
      try {
        let response
        if (isTeacher) {
          response = await api.get("/classes")
        } else {
          response = await api.get(`/classes/student/${user?.id}`)
        }
        setClasses(response.data)
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar as turmas.",
        })
      } finally {
        setIsLoading(false)
      }
    }

    const fetchSubjectsAndAcademicYears = async () => {
      if (isTeacher) {
        try {
          const [subjectsResponse, academicYearsResponse] = await Promise.all([
            api.get("/subjects"),
            api.get("/academic-years"),
          ])
          setSubjects(subjectsResponse.data)
          setAcademicYears(academicYearsResponse.data)
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Erro",
            description: "Não foi possível carregar os dados para criar turmas.",
          })
        }
      }
    }

    fetchClasses()
    fetchSubjectsAndAcademicYears()
  }, [isAuthenticated, isTeacher, router, toast, user])

  const handleCreateClass = async () => {
    if (!newClassName || !selectedSubject || !selectedAcademicYear) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Por favor, preencha todos os campos.",
      })
      return
    }

    try {
      const response = await api.post("/classes", {
        name: newClassName,
        subjectId: Number.parseInt(selectedSubject),
        academicYearId: Number.parseInt(selectedAcademicYear),
      })

      setClasses([...classes, response.data])
      setIsDialogOpen(false)
      setNewClassName("")
      setSelectedSubject("")
      setSelectedAcademicYear("")

      toast({
        title: "Turma criada",
        description: "A turma foi criada com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível criar a turma.",
      })
    }
  }

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar turmas...</p>
          </div>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-[#1E40AF]">Turmas</h1>
          {isTeacher && (
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">Criar Turma</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Criar Nova Turma</DialogTitle>
                  <DialogDescription>Preencha os detalhes para criar uma nova turma.</DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <Label htmlFor="name">Nome da Turma</Label>
                    <Input
                      id="name"
                      value={newClassName}
                      onChange={(e) => setNewClassName(e.target.value)}
                      placeholder="Ex: Matemática 10º Ano - Turma A"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="subject">Disciplina</Label>
                    <Select value={selectedSubject} onValueChange={setSelectedSubject}>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione uma disciplina" />
                      </SelectTrigger>
                      <SelectContent>
                        {subjects.map((subject) => (
                          <SelectItem key={subject.id} value={subject.id.toString()}>
                            {subject.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="academicYear">Ano Letivo</Label>
                    <Select value={selectedAcademicYear} onValueChange={setSelectedAcademicYear}>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione um ano letivo" />
                      </SelectTrigger>
                      <SelectContent>
                        {academicYears.map((year) => (
                          <SelectItem key={year.id} value={year.id.toString()}>
                            {year.name}
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
                  <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={handleCreateClass}>
                    Criar
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        {classes.length > 0 ? (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {classes.map((classe) => (
              <Card key={classe.id}>
                <CardHeader>
                  <CardTitle className="text-lg">{classe.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Disciplina:</span> {classe.subjectName}
                    </div>
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Ano Letivo:</span> {classe.academicYearName}
                    </div>
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Data de Criação:</span>{" "}
                      {new Date(classe.creationDate).toLocaleDateString("pt-PT")}
                    </div>
                  </div>
                </CardContent>
                <CardFooter>
                  <Button asChild variant="outline" className="w-full">
                    <Link href={`/classes/${classe.id}`}>Ver Detalhes</Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-lg text-gray-500 mb-4">Não existem turmas disponíveis.</p>
            {isTeacher && (
              <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={() => setIsDialogOpen(true)}>
                Criar a Primeira Turma
              </Button>
            )}
          </div>
        )}
      </div>
    </AppLayout>
  )
}
