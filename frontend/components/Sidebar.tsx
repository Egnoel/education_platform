"use client"

import { useState } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { useAuth } from "@/lib/context/AuthContext"
import { Button } from "@/components/ui/button"
import { LayoutDashboard, Users, BookOpen, FileQuestion, GraduationCap, ChevronLeft, ChevronRight } from "lucide-react"
import { useMobile } from "@/hooks/use-mobile"

export default function Sidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const pathname = usePathname()
  const { user } = useAuth()
  const isMobile = useMobile()

  const isTeacher = user?.role === "TEACHER"

  const navItems = [
    {
      title: "Painel de Controlo",
      href: "/dashboard",
      icon: <LayoutDashboard className="h-5 w-5" />,
    },
    {
      title: "Turmas",
      href: "/classes",
      icon: <Users className="h-5 w-5" />,
    },
    {
      title: "Materiais",
      href: "/materials",
      icon: <BookOpen className="h-5 w-5" />,
    },
    {
      title: "Quizzes",
      href: "/quizzes",
      icon: <FileQuestion className="h-5 w-5" />,
    },
    {
      title: "Avaliações",
      href: "/assessments",
      icon: <GraduationCap className="h-5 w-5" />,
    },
  ]

  // Don't render sidebar on public pages
  if (pathname === "/" || pathname === "/login" || pathname === "/register") {
    return null
  }

  // On mobile, sidebar is hidden by default
  if (isMobile && !isCollapsed) {
    return (
      <Button
        variant="outline"
        size="icon"
        className="fixed bottom-4 right-4 z-50 rounded-full shadow-md"
        onClick={() => setIsCollapsed(false)}
      >
        <ChevronLeft className="h-5 w-5" />
      </Button>
    )
  }

  return (
    <aside
      className={`${
        isCollapsed ? "w-16" : "w-64"
      } fixed inset-y-0 left-0 z-40 flex flex-col border-r bg-white transition-all duration-300 ${
        isMobile ? "translate-x-0" : ""
      }`}
    >
      <div className="flex h-16 items-center justify-between border-b px-4">
        {!isCollapsed && <span className="text-lg font-bold text-[#1E40AF]">EduConnect</span>}
        <Button variant="ghost" size="icon" onClick={() => setIsCollapsed(!isCollapsed)} className="ml-auto">
          {isCollapsed ? <ChevronRight className="h-5 w-5" /> : <ChevronLeft className="h-5 w-5" />}
        </Button>
      </div>
      <nav className="flex-1 overflow-y-auto p-2">
        <ul className="space-y-1">
          {navItems.map((item) => (
            <li key={item.href}>
              <Link
                href={item.href}
                className={`flex items-center rounded-md px-3 py-2 ${
                  pathname === item.href || pathname.startsWith(`${item.href}/`)
                    ? "bg-[#EBF5FF] text-[#1E40AF]"
                    : "text-gray-500 hover:bg-gray-100"
                } ${isCollapsed ? "justify-center" : ""}`}
              >
                {item.icon}
                {!isCollapsed && <span className="ml-3">{item.title}</span>}
              </Link>
            </li>
          ))}
        </ul>
      </nav>
      <div className="border-t p-2">
        <Link
          href="/profile"
          className={`flex items-center rounded-md px-3 py-2 ${
            pathname === "/profile" ? "bg-[#EBF5FF] text-[#1E40AF]" : "text-gray-500 hover:bg-gray-100"
          } ${isCollapsed ? "justify-center" : ""}`}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
            />
          </svg>
          {!isCollapsed && <span className="ml-3">Perfil</span>}
        </Link>
      </div>
    </aside>
  )
}
