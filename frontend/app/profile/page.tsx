"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/lib/context/AuthContext"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useToast } from "@/hooks/use-toast"
import { api } from "@/lib/api"
import AppLayout from "@/components/AppLayout"

interface Institution {
  id: number
  name: string
}

export default function ProfilePage() {
  const [firstName, setFirstName] = useState("")
  const [lastName, setLastName] = useState("")
  const [email, setEmail] = useState("")
  const [institution, setInstitution] = useState<Institution | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const { user, isAuthenticated, updateUserInfo } = useAuth()
  const router = useRouter()
  const { toast } = useToast()

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login")
      return
    }

    const fetchUserProfile = async () => {
      try {
        // Assuming the backend has an endpoint to get user profile details
        const response = await api.get(`/api/auth/profile`)
        const userData = response.data

        setFirstName(userData.firstName)
        setLastName(userData.lastName)
        setEmail(userData.email)
        setInstitution(userData.institution)
      } catch (error) {
        // If the endpoint doesn't exist, use the data from auth context
        if (user) {
          setFirstName(user.firstName)
          setLastName(user.lastName)
          setEmail(user.email)
        }

        // Try to fetch institution separately if needed
        if (user?.institutionId) {
          try {
            const institutionResponse = await api.get(`/institutions/${user.institutionId}`)
            setInstitution(institutionResponse.data)
          } catch (institutionError) {
            console.error("Failed to fetch institution:", institutionError)
          }
        }
      } finally {
        setIsLoading(false)
      }
    }

    fetchUserProfile()
  }, [isAuthenticated, router, user])

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSaving(true)

    try {
      // Assuming the backend has an endpoint to update user profile
      const response = await api.put("/api/auth/profile", {
        firstName,
        lastName,
        email,
      })

      // Update the user info in the auth context
      updateUserInfo({
        ...user!,
        firstName,
        lastName,
        email,
      })

      toast({
        title: "Perfil atualizado",
        description: "As suas informações foram atualizadas com sucesso.",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: "Não foi possível atualizar o seu perfil.",
      })
    } finally {
      setIsSaving(false)
    }
  }

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar perfil...</p>
          </div>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <h1 className="text-2xl font-bold mb-6 text-[#1E40AF]">Perfil</h1>

        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Informações Pessoais</CardTitle>
              <CardDescription>Atualize as suas informações pessoais aqui.</CardDescription>
            </CardHeader>
            <form onSubmit={handleUpdateProfile}>
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
                  <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="institution">Instituição</Label>
                  <Input id="institution" value={institution?.name || ""} disabled />
                  <p className="text-xs text-gray-500">
                    A instituição não pode ser alterada. Contacte o administrador se necessário.
                  </p>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="role">Função</Label>
                  <Input id="role" value={user?.role === "TEACHER" ? "Professor" : "Aluno"} disabled />
                  <p className="text-xs text-gray-500">
                    A função não pode ser alterada. Contacte o administrador se necessário.
                  </p>
                </div>
              </CardContent>
              <CardFooter>
                <Button type="submit" className="w-full bg-[#1E40AF] hover:bg-[#1E3A8A]" disabled={isSaving}>
                  {isSaving ? "A guardar..." : "Guardar Alterações"}
                </Button>
              </CardFooter>
            </form>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Segurança</CardTitle>
              <CardDescription>Gerencie as suas definições de segurança.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="current-password">Palavra-passe Atual</Label>
                <Input id="current-password" type="password" placeholder="••••••••" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="new-password">Nova Palavra-passe</Label>
                <Input id="new-password" type="password" placeholder="••••••••" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="confirm-password">Confirmar Nova Palavra-passe</Label>
                <Input id="confirm-password" type="password" placeholder="••••••••" />
              </div>
            </CardContent>
            <CardFooter>
              <Button className="w-full bg-[#1E40AF] hover:bg-[#1E3A8A]">Alterar Palavra-passe</Button>
            </CardFooter>
          </Card>
        </div>
      </div>
    </AppLayout>
  )
}
