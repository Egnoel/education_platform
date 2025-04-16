import Link from "next/link"
import { Button } from "@/components/ui/button"
import Navbar from "@/components/Navbar"

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1">
        <section className="py-12 md:py-24 lg:py-32 bg-white">
          <div className="container px-4 md:px-6">
            <div className="flex flex-col items-center space-y-4 text-center">
              <div className="space-y-2">
                <h1 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl lg:text-6xl text-[#1E40AF]">
                  Bem-vindo ao EduConnect
                </h1>
                <p className="mx-auto max-w-[700px] text-gray-500 md:text-xl">A sua plataforma educacional</p>
              </div>
              <div className="space-x-4">
                <Link href="/login">
                  <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">Entrar</Button>
                </Link>
                <Link href="/register">
                  <Button variant="outline" className="border-[#1E40AF] text-[#1E40AF]">
                    Registar
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </section>

        <section className="py-12 md:py-24 lg:py-32 bg-gray-50">
          <div className="container px-4 md:px-6">
            <div className="mx-auto grid max-w-5xl items-center gap-6 py-12 lg:grid-cols-2 lg:gap-12">
              <div className="space-y-4">
                <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl text-[#1E40AF]">
                  Conecte professores e alunos
                </h2>
                <p className="text-gray-500 md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
                  O EduConnect facilita a gestão de turmas, materiais, quizzes e avaliações, proporcionando uma
                  experiência educacional integrada para instituições de ensino.
                </p>
              </div>
              <div className="flex justify-center">
                <div className="w-full max-w-md rounded-lg border border-gray-200 bg-white p-6 shadow-md">
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <h3 className="text-xl font-bold text-[#1E40AF]">Funcionalidades</h3>
                      <ul className="list-disc pl-5 space-y-2 text-gray-500">
                        <li>Gestão de turmas e alunos</li>
                        <li>Partilha de materiais didáticos</li>
                        <li>Criação e avaliação de quizzes</li>
                        <li>Registo e consulta de avaliações</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>

      <footer className="border-t bg-white py-6 md:py-8">
        <div className="container px-4 md:px-6">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div className="text-center md:text-left">
              <p className="text-sm text-gray-500">
                &copy; {new Date().getFullYear()} EduConnect. Todos os direitos reservados.
              </p>
            </div>
            <div className="flex gap-4">
              <Link href="#" className="text-sm text-gray-500 hover:underline">
                Sobre
              </Link>
              <Link href="#" className="text-sm text-gray-500 hover:underline">
                Contacto
              </Link>
              <Link href="#" className="text-sm text-gray-500 hover:underline">
                Termos
              </Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
