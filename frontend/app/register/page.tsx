"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import { api } from "@/lib/api"
import Navbar from "@/components/Navbar"

interface Institution {
  id: number
  name: string
}

export default function RegisterPage() {
  const [firstName, setFirstName] = useState("")
  const [lastName, setLastName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [institutionId, setInstitutionId] = useState("")
  const [role, setRole] = useState("")
  const [institutions, setInstitutions] = useState<Institution[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()
  const { toast } = useToast()

  useEffect(() => {
    // Fetch institutions for the dropdown
    const fetchInstitutions = async () => {
      try {
        const response = await api.get("/institutions")
        setInstitutions(response.data)
      } catch (error) {
        console.error("Failed to fetch institutions:", error)
        toast({
          variant: "destructive",
          title: "Erro",
          description: "Não foi possível carregar as instituições.",
        })
      }
    }

    fetchInstitutions()
  }, [toast])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      await api.post("/api/auth/register", {
        firstName,
        lastName,
        email,
        password,
        institutionId: Number.parseInt(institutionId),
        role,
      })

      toast({
        title: "Registo bem-sucedido",
        description: "A sua conta foi criada com sucesso. Pode entrar agora.",
      })
      router.push("/login")
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro no registo",
        description: "Não foi possível criar a sua conta. Por favor, tente novamente.",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 flex items-center justify-center p-4 py-8">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-2xl text-center text-[#1E40AF]">Registar</CardTitle>
            <CardDescription className="text-center">
              Crie a sua conta para aceder à plataforma EduConnect
            </CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="firstName">Nome</Label>
                  <Input id="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName">Apelido</Label>
                  <Input id="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="seu@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Palavra-passe</Label>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="institution">Instituição</Label>
                <Select value={institutionId} onValueChange={setInstitutionId} required>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione uma instituição" />
                  </SelectTrigger>
                  <SelectContent>
                    {institutions.map((institution) => (
                      <SelectItem key={institution.id} value={institution.id.toString()}>
                        {institution.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="role">Função</Label>
                <Select value={role} onValueChange={setRole} required>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione a sua função" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="STUDENT">Aluno</SelectItem>
                    <SelectItem value="TEACHER">Professor</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col space-y-4">
              <Button type="submit" className="w-full bg-[#1E40AF] hover:bg-[#1E3A8A]" disabled={isLoading}>
                {isLoading ? "A processar..." : "Registar"}
              </Button>
              <div className="text-center text-sm">
                Já tem uma conta?{" "}
                <Link href="/login" className="text-[#60A5FA] hover:underline">
                  Entrar
                </Link>
              </div>
            </CardFooter>
          </form>
        </Card>
      </main>
    </div>
  )
}
