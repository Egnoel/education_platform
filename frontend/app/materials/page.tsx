'use client';

import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAuth } from '@/lib/context/AuthContext';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useToast } from '@/hooks/use-toast';
import { api } from '@/lib/api';
import AppLayout from '@/components/AppLayout';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import Link from 'next/link';

interface Material {
  id: number;
  title: string;
  content: string;
  classeName: string;
  subjectName: string;
  uploadDate: string;
}

interface Class {
  id: number;
  name: string;
}

interface Subject {
  id: number;
  name: string;
}

export default function MaterialsPage() {
  const searchParams = useSearchParams();
  const classeId = searchParams.get('classeId');

  const [materials, setMaterials] = useState<Material[]>([]);
  const [classes, setClasses] = useState<Class[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [selectedClass, setSelectedClass] = useState(classeId || '');
  const [selectedSubject, setSelectedSubject] = useState('');
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const { toast } = useToast();

  const isTeacher = user?.role === 'TEACHER';

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchMaterials = async () => {
      try {
        let url = '/materials';
        if (classeId) {
          url += `?classeId=${classeId}`;
        }
        const response = await api.get(url);
        setMaterials(response);
      } catch (error) {
        toast({
          variant: 'destructive',
          title: 'Erro',
          description: 'Não foi possível carregar os materiais.',
        });
      } finally {
        setIsLoading(false);
      }
    };

    const fetchClassesAndSubjects = async () => {
      if (isTeacher) {
        try {
          const [classesResponse, subjectsResponse] = await Promise.all([
            api.get('/classes'),
            api.get('/subjects'),
          ]);
          setClasses(classesResponse.data);
          setSubjects(subjectsResponse.data);
        } catch (error) {
          toast({
            variant: 'destructive',
            title: 'Erro',
            description:
              'Não foi possível carregar os dados para criar materiais.',
          });
        }
      }
    };

    fetchMaterials();
    fetchClassesAndSubjects();
  }, [classeId, isAuthenticated, isTeacher, router, toast]);

  const handleCreateMaterial = async () => {
    if (!title || !content || !selectedClass || !selectedSubject) {
      toast({
        variant: 'destructive',
        title: 'Erro',
        description: 'Por favor, preencha todos os campos.',
      });
      return;
    }

    try {
      const response = await api.post('/materials', {
        title,
        content,
        classeId: Number.parseInt(selectedClass),
        subjectId: Number.parseInt(selectedSubject),
      });

      setMaterials([...materials, response.data]);
      setIsDialogOpen(false);
      setTitle('');
      setContent('');
      setSelectedClass(classeId || '');
      setSelectedSubject('');

      toast({
        title: 'Material criado',
        description: 'O material foi criado com sucesso.',
      });
    } catch (error) {
      toast({
        variant: 'destructive',
        title: 'Erro',
        description: 'Não foi possível criar o material.',
      });
    }
  };

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <p className="text-lg text-gray-500">A carregar materiais...</p>
          </div>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="container px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-[#1E40AF]">Materiais</h1>
          {isTeacher && (
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="bg-[#1E40AF] hover:bg-[#1E3A8A]">
                  Criar Material
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[550px]">
                <DialogHeader>
                  <DialogTitle>Criar Novo Material</DialogTitle>
                  <DialogDescription>
                    Preencha os detalhes para criar um novo material didático.
                  </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <Label htmlFor="title">Título</Label>
                    <Input
                      id="title"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      placeholder="Ex: Introdução à Álgebra"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="content">Conteúdo</Label>
                    <Textarea
                      id="content"
                      value={content}
                      onChange={(e) => setContent(e.target.value)}
                      placeholder="Conteúdo do material..."
                      rows={5}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="class">Turma</Label>
                    <Select
                      value={selectedClass}
                      onValueChange={setSelectedClass}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione uma turma" />
                      </SelectTrigger>
                      <SelectContent>
                        {classes.map((classe) => (
                          <SelectItem
                            key={classe.id}
                            value={classe.id.toString()}
                          >
                            {classe.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="subject">Disciplina</Label>
                    <Select
                      value={selectedSubject}
                      onValueChange={setSelectedSubject}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Selecione uma disciplina" />
                      </SelectTrigger>
                      <SelectContent>
                        {subjects.map((subject) => (
                          <SelectItem
                            key={subject.id}
                            value={subject.id.toString()}
                          >
                            {subject.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setIsDialogOpen(false)}
                  >
                    Cancelar
                  </Button>
                  <Button
                    className="bg-[#1E40AF] hover:bg-[#1E3A8A]"
                    onClick={handleCreateMaterial}
                  >
                    Criar
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        {materials && materials.length > 0 ? (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {materials.map((material) => (
              <Card key={material.id}>
                <CardHeader>
                  <CardTitle className="text-lg">{material.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Turma:</span>{' '}
                      {material.classeName}
                    </div>
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Disciplina:</span>{' '}
                      {material.subjectName}
                    </div>
                    <div className="text-sm text-gray-500">
                      <span className="font-medium">Data de Criação:</span>{' '}
                      {new Date(material.uploadDate).toLocaleDateString(
                        'pt-PT'
                      )}
                    </div>
                  </div>
                </CardContent>
                <CardFooter>
                  <Button asChild variant="outline" className="w-full">
                    <Link href={`/materials/${material.id}`}>Ver Material</Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-lg text-gray-500 mb-4">
              Não existem materiais disponíveis.
            </p>
            {isTeacher && (
              <Button
                className="bg-[#1E40AF] hover:bg-[#1E3A8A]"
                onClick={() => setIsDialogOpen(true)}
              >
                Criar o Primeiro Material
              </Button>
            )}
          </div>
        )}
      </div>
    </AppLayout>
  );
}
