"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
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

interface Quiz {
  id: number
  title: string
  classeName: string
  creationDate: string
  hasPendingAnswers?: boolean
}

interface Class {
  id: number
  name: string
}

export default function QuizzesPage() {
  const searchParams = useSearchParams()
  const classeId = searchParams.get("classeId")

  const [quizzes, setQuizzes] = useState<Quiz[]>([])
  const [classes, setClasses] = useState<Class[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [title, setTitle] = useState("")
  const [selectedClass, setSelectedClass] = useState(classeId || "")
  const { user, isAuthenticated } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  const isTeacher = user?.role === "TEACHER"

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login")
      return
    }

    const fetchQuizzes = async () => {
      try {
        let url = "/quizzes"
        if (classeId) {
          url += `?classeId=${classeId}`
        }
        const response = await api.get(url)
        setQuizzes(response.data)
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar os quizzes.",
        })
      } finally {
        setIsLoading(false)
      }
    }

    const fetchClasses = async () => {
      if (isTeacher) {
        try {
          const response = await api.get("/classes")
          setClasses(response.data)
        } catch (error) {
          toast({
            variant: "destructive",
            title: "Erro",
            description: "Não foi possível carregar as turmas.",
          })
        }
      }
    }

    fetchQuizzes()
    fetchClasses()
  }, [classeId, isAuthenticated, isTeacher, router, toast])

  const handleCreateQuiz = async () => {
    if (!title || !selectedClass) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Por favor, preencha todos os campos.",
      })
      return
    }

    try {
      const response = await api.post("/quizzes", {
        title,
        classeId: Number.parseInt(selectedClass),
      })

      setQuizzes([...quizzes, response.data])
      setIsDialogOpen(false)
      setTitle("")
      setSelectedClass(classeId || "")

      toast({
        title: "Quiz criado",
        description: "O quiz foi criado com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível criar o quiz.",
      })
    }
  }

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar quizzes...</p>
          </div>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-[#1E40AF]">Quizzes</h1>
          {isTeacher && (
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">Criar Quiz</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Criar Novo Quiz</DialogTitle>
                  <DialogDescription>Preencha os detalhes para criar um novo quiz.</DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <Label htmlFor="title">Título</Label>
                    <Input
                      id="title"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      placeholder="Ex: Quiz de Revisão - Capítulo 3"
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
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancelar
                  </Button>
                  <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={handleCreateQuiz}>
                    Criar
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        {quizzes.length > 0 ? (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {quizzes.map((quiz) => (
              <Card key={quiz.id}>
                <CardHeader>
                  <CardTitle className="text-lg">{quiz.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Turma:</span> {quiz.classeName}
                    </div>
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Data de Criação:</span>{" "}
                      {new Date(quiz.creationDate).toLocaleDateString("pt-PT")}
                    </div>
                    {!isTeacher && quiz.hasPendingAnswers !== undefined && (
                      <div className={`text-sm ${quiz.hasPendingAnswers ? "text-amber-500" : "text-green-500"}`}>
                        <span className="font-medium">Estado:</span> {quiz.hasPendingAnswers ? "Pendente" : "Concluído"}
                      </div>
                    )}
                  </div>
                </CardContent>
                <CardFooter>
                  <Button asChild variant="outline" className="w-full">
                    <Link href={`/quizzes/${quiz.id}`}>{isTeacher ? "Ver Respostas" : "Responder"}</Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-lg text-gray-500 mb-4">Não existem quizzes disponíveis.</p>
            {isTeacher && (
              <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]" onClick={() => setIsDialogOpen(true)}>
                Criar o Primeiro Quiz
              </Button>
            )}
          </div>
        )}
      </div>
    </AppLayout>
  )
}
