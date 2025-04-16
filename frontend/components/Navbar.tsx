"use client"

import { useState } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { useAuth } from "@/lib/context/AuthContext"
import { Button } from "@/components/ui/button"
import { Menu } from "lucide-react"

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const pathname = usePathname()
  const { user, isAuthenticated, logout } = useAuth()

  const isHomePage = pathname === "/"

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-white">
      <div className="container flex h-16 items-center justify-between px-4">
        <div className="flex items-center">
          <Link href="/" className="flex items-center">
            <span className="text-xl font-bold text-[#1E40AF]">EduConnect</span>
          </Link>
        </div>

        <div className="md:hidden">
          <Button variant="ghost" size="icon" onClick={() => setIsMenuOpen(!isMenuOpen)} aria-label="Toggle menu">
            <Menu className="h-6 w-6" />
          </Button>
        </div>

        <nav
          className={`${isMenuOpen ? "flex" : "hidden"} absolute top-16 left-0 right-0 flex-col gap-2 border-b bg-white p-4 md:static md:flex md:flex-row md:items-center md:border-0 md:p-0`}
        >
          <Link
            href="/"
            className={`text-sm font-medium ${pathname === "/" ? "text-[#1E40AF]" : "text-gray-500 hover:text-gray-900"}`}
          >
            Início
          </Link>

          {isAuthenticated ? (
            <>
              <Link
                href="/dashboard"
                className={`text-sm font-medium ${pathname === "/dashboard" ? "text-[#1E40AF]" : "text-gray-500 hover:text-gray-900"}`}
              >
                Painel de Controlo
              </Link>
              <Link
                href="/profile"
                className={`text-sm font-medium ${pathname === "/profile" ? "text-[#1E40AF]" : "text-gray-500 hover:text-gray-900"}`}
              >
                Perfil
              </Link>
              <Button
                variant="ghost"
                className="text-sm font-medium text-gray-500 hover:text-gray-900"
                onClick={logout}
              >
                Sair
              </Button>
            </>
          ) : (
            !isHomePage && (
              <>
                <Link href="/login">
                  <Button variant="ghost" className="text-sm font-medium">
                    Entrar
                  </Button>
                </Link>
                <Link href="/register">
                  <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A] text-sm font-medium">Registar</Button>
                </Link>
              </>
            )
          )}
        </nav>
      </div>
    </header>
  )
}
