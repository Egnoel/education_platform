package com.egnoel.backend.modules.classe.service;

import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.academicyear.repository.AcademicYearRepository;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.repository.StudentRepository;
import com.egnoel.backend.modules.auth.repository.TeacherRepository;
import com.egnoel.backend.modules.auth.repository.UserRepository;
import com.egnoel.backend.modules.classe.dto.*;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.classe.repository.ClasseRepository;
import com.egnoel.backend.modules.subject.entity.Subject;
import com.egnoel.backend.modules.subject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClasseService {
    private final ClasseRepository classeRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public ClasseService(ClasseRepository classeRepository, TeacherRepository teacherRepository,
                         SubjectRepository subjectRepository, AcademicYearRepository academicYearRepository,
                         UserRepository userRepository,  StudentRepository studentRepository) {
        this.classeRepository = classeRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.academicYearRepository = academicYearRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClasseResponseDTO createClasse(ClasseCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new RuntimeException("Ano letivo não encontrado"));

        if (!academicYear.isActive()) {
            throw new RuntimeException("Não é possível criar turmas em um ano letivo inativo");
        }

        if (!teacher.getInstitution().getId().equals(academicYear.getInstitution().getId())) {
            throw new RuntimeException("O professor deve pertencer à mesma instituição do ano letivo");
        }

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        Classe classe = new Classe();
        classe.setName(dto.getName());
        classe.setAcademicYear(academicYear);
        classe.setSubject(subject);
        classe.setTeacher(teacher);

        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                academicYear.getName(),
                subject.getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                List.of()
        );
    }

    @Transactional
    public ClasseResponseDTO addStudentsToClasse(Long classeId, AddStudentsToClasseDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = (Teacher) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor da turma pode adicionar alunos");
        }

        List<Student> students = userRepository.findAllById(dto.getStudentIds()).stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());

        if (students.size() != dto.getStudentIds().size()) {
            throw new RuntimeException("Um ou mais alunos não foram encontrados");
        }

        for (Student student : students) {
            if (!student.getInstitution().getId().equals(classe.getAcademicYear().getInstitution().getId())) {
                throw new RuntimeException("O aluno " + student.getFirstName() + " " + student.getLastName() + " não pertence à mesma instituição da turma");
            }
            if (!classe.getStudents().contains(student)) {
                classe.getStudents().add(student);
            }
        }

        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                classe.getAcademicYear().getName(),
                classe.getSubject().getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                classe.getStudents().stream()
                        .map(s -> s.getFirstName() + " " + s.getLastName())
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public ClasseResponseDTO removeStudentsFromClasse(Long classeId, AddStudentsToClasseDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = (Teacher) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor da turma pode remover alunos");
        }

        List<Student> students = userRepository.findAllById(dto.getStudentIds()).stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .toList();

        if (students.size() != dto.getStudentIds().size()) {
            throw new RuntimeException("Um ou mais alunos não foram encontrados");
        }

        for (Student student : students) {
            if (!classe.getStudents().contains(student)) {
                throw new RuntimeException("O aluno " + student.getFirstName() + " " + student.getLastName() + " não está inscrito nesta turma");
            }
            classe.getStudents().remove(student);
        }

        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                classe.getAcademicYear().getName(),
                classe.getSubject().getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                classe.getStudents().stream()
                        .map(s -> s.getFirstName() + " " + s.getLastName())
                        .collect(Collectors.toList())
        );
    }

    public List<ClasseResponseDTO> listClassesByStudent(Long studentId) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return classeRepository.findByStudentId(studentId).stream()
                .map(c -> new ClasseResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getCreationDate(),
                        c.getAcademicYear().getName(),
                        c.getSubject().getName(),
                        c.getTeacher().getFirstName() + " " + c.getTeacher().getLastName(),
                        c.getStudents().stream()
                                .map(s -> s.getFirstName() + " " + s.getLastName())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClasseResponseDTO updateClasse(Long id, ClasseUpdateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor responsável pode editar esta turma");
        }

        classe.setName(dto.getName());
        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                classe.getAcademicYear().getName(),
                classe.getSubject().getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                classe.getStudents().stream().map(Student::getFirstName).collect(Collectors.toList())
        );
    }

    @Transactional
    public void deleteClasse(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor responsável pode excluir esta turma");
        }

        classeRepository.delete(classe);
    }

    public List<ClasseResponseDTO> listClasses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Classe> classes;

        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            Teacher teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            classes = classeRepository.findByTeacherId(teacher.getId());
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            classes = student.getClasses();
        } else {
            classes = classeRepository.findAll();
        }

        return classes.stream()
                .map(c -> new ClasseResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getCreationDate(),
                        c.getAcademicYear().getName(),
                        c.getSubject().getName(),
                        c.getTeacher().getFirstName() + " " + c.getTeacher().getLastName(),
                        c.getStudents().stream().map(Student::getFirstName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClasseResponseDTO addStudents(Long classeId, AddStudentsDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor responsável pode adicionar alunos");
        }

        List<Student> students = studentRepository.findAllById(dto.getStudentIds());
        if (students.size() != dto.getStudentIds().size()) {
            throw new RuntimeException("Um ou mais alunos não foram encontrados");
        }

        classe.getStudents().addAll(students);
        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                classe.getAcademicYear().getName(),
                classe.getSubject().getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                classe.getStudents().stream().map(Student::getFirstName).collect(Collectors.toList())
        );
    }

    @Transactional
    public ClasseResponseDTO removeStudents(Long classeId, AddStudentsDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        if (!classe.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Apenas o professor responsável pode remover alunos");
        }

        List<Student> studentsToRemove = studentRepository.findAllById(dto.getStudentIds());
        classe.getStudents().removeAll(studentsToRemove);
        classe = classeRepository.save(classe);

        return new ClasseResponseDTO(
                classe.getId(),
                classe.getName(),
                classe.getCreationDate(),
                classe.getAcademicYear().getName(),
                classe.getSubject().getName(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                classe.getStudents().stream().map(Student::getFirstName).collect(Collectors.toList())
        );
    }
}
