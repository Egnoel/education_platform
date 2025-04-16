'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/context/AuthContext';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useToast } from '@/hooks/use-toast';
import { api } from '@/lib/api';
import AppLayout from '@/components/AppLayout';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import Link from 'next/link';

interface DashboardData {
  userName: string;
  role: string;
  classes: {
    id: number;
    name: string;
    subjectName: string;
    academicYearName: string;
    creationDate: string;
  }[];
  materials: {
    id: number;
    title: string;
    classeName: string;
    subjectName: string;
    creationDate: string;
  }[];
  quizzes: {
    id: number;
    title: string;
    classeName: string;
    creationDate: string;
    hasPendingAnswers: boolean;
  }[];
  assessments: {
    id: number;
    title: string;
    grade: number;
    classeName: string;
    studentName: string;
    date: string;
  }[];
}

export default function DashboardPage() {
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(
    null
  );
  const [isLoading, setIsLoading] = useState(true);
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const { toast } = useToast();

  useEffect(() => {
    /*
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
*/
    const fetchDashboardData = async () => {
      try {
        const response = await api.get('/dashboard');
        setDashboardData(response);
      } catch (error) {
        toast({
          variant: 'destructive',
          title: 'Erro',
          description: 'Não foi possível carregar os dados do painel.',
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboardData();
  }, [isAuthenticated, router, toast]);

  const isTeacher = user?.role === 'TEACHER';

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar dados...</p>
          </div>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <h1 className="text-2xl font-bold mb-6 text-[#1E40AF]">
          Painel de Controlo
        </h1>

        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {/* Classes Section */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-lg">As Suas Turmas</CardTitle>
              <CardDescription>
                {isTeacher
                  ? 'Turmas que está a lecionar'
                  : 'Turmas em que está inscrito'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {dashboardData?.classes && dashboardData.classes.length > 0 ? (
                <div className="space-y-2">
                  {dashboardData.classes.slice(0, 3).map((classe) => (
                    <div key={classe.id} className="p-3 border rounded-md">
                      <div className="font-medium">{classe.name}</div>
                      <div className="text-sm text-gray-500">
                        {classe.subjectName}
                      </div>
                      <div className="text-sm text-gray-500">
                        {classe.academicYearName}
                      </div>
                    </div>
                  ))}
                  <Button asChild variant="outline" className="w-full mt-2">
                    <Link href="/classes">Ver todas as turmas</Link>
                  </Button>
                </div>
              ) : (
                <div className="text-center py-4 text-gray-500">
                  Não existem turmas disponíveis.
                </div>
              )}
            </CardContent>
          </Card>

          {/* Materials Section */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-lg">Materiais Recentes</CardTitle>
              <CardDescription>
                {isTeacher
                  ? 'Materiais que criou recentemente'
                  : 'Materiais disponíveis para si'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {dashboardData?.materials &&
              dashboardData.materials.length > 0 ? (
                <div className="space-y-2">
                  {dashboardData.materials.slice(0, 3).map((material) => (
                    <div key={material.id} className="p-3 border rounded-md">
                      <div className="font-medium">{material.title}</div>
                      <div className="text-sm text-gray-500">
                        {material.classeName}
                      </div>
                      <div className="text-sm text-gray-500">
                        {material.subjectName}
                      </div>
                    </div>
                  ))}
                  <Button asChild variant="outline" className="w-full mt-2">
                    <Link href="/materials">Ver todos os materiais</Link>
                  </Button>
                </div>
              ) : (
                <div className="text-center py-4 text-gray-500">
                  Não existem materiais disponíveis.
                </div>
              )}
            </CardContent>
          </Card>

          {/* Quizzes Section */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-lg">Quizzes</CardTitle>
              <CardDescription>
                {isTeacher
                  ? 'Quizzes que criou'
                  : 'Quizzes pendentes e concluídos'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {dashboardData?.quizzes && dashboardData.quizzes.length > 0 ? (
                <div className="space-y-2">
                  {dashboardData.quizzes.slice(0, 3).map((quiz) => (
                    <div key={quiz.id} className="p-3 border rounded-md">
                      <div className="font-medium">{quiz.title}</div>
                      <div className="text-sm text-gray-500">
                        {quiz.classeName}
                      </div>
                      {!isTeacher && (
                        <div
                          className={`text-sm ${
                            quiz.hasPendingAnswers
                              ? 'text-amber-500'
                              : 'text-green-500'
                          }`}
                        >
                          {quiz.hasPendingAnswers ? 'Pendente' : 'Concluído'}
                        </div>
                      )}
                    </div>
                  ))}
                  <Button asChild variant="outline" className="w-full mt-2">
                    <Link href="/quizzes">Ver todos os quizzes</Link>
                  </Button>
                </div>
              ) : (
                <div className="text-center py-4 text-gray-500">
                  Não existem quizzes disponíveis.
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Assessments Section */}
        <Card className="mt-6">
          <CardHeader>
            <CardTitle className="text-lg">Avaliações Recentes</CardTitle>
            <CardDescription>
              {isTeacher
                ? 'Avaliações atribuídas recentemente'
                : 'As suas avaliações recentes'}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {dashboardData?.assessments &&
            dashboardData.assessments.length > 0 ? (
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
                  {dashboardData.assessments.map((assessment) => (
                    <TableRow key={assessment.id}>
                      <TableCell className="font-medium">
                        {assessment.title}
                      </TableCell>
                      {isTeacher && (
                        <TableCell>{assessment.studentName}</TableCell>
                      )}
                      <TableCell>{assessment.classeName}</TableCell>
                      <TableCell>{assessment.grade}</TableCell>
                      <TableCell>
                        {new Date(assessment.date).toLocaleDateString('pt-PT')}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <div className="text-center py-4 text-gray-500">
                Não existem avaliações disponíveis.
              </div>
            )}
            <div className="mt-4">
              <Button asChild variant="outline">
                <Link href="/assessments">Ver todas as avaliações</Link>
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </AppLayout>
  );
}
