package com.egnoel.backend.modules.auth.service;

import com.egnoel.backend.core.util.JwtUtil;
import com.egnoel.backend.modules.auth.dto.*;
import com.egnoel.backend.modules.auth.entity.Admin;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.auth.repository.AdminRepository;
import com.egnoel.backend.modules.auth.repository.StudentRepository;
import com.egnoel.backend.modules.auth.repository.TeacherRepository;
import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.institution.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(TeacherRepository teacherRepository,
                     StudentRepository studentRepository,
                     InstitutionRepository institutionRepository,
                        AdminRepository adminRepository,
                     PasswordEncoder passwordEncoder,
                     JwtUtil jwtUtil) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.institutionRepository = institutionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public AuthResponseDTO registerTeacher(TeacherRegisterDTO dto){

        if(teacherRepository.findByEmail(dto.getEmail()).isPresent() ||
                studentRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email already in use");
        }

        var institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));

        Teacher teacher = new Teacher();
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setInstitution(institution);

        teacherRepository.save(teacher);

        String employeeNumber = String.format("PROF-%03d", teacher.getId());
        teacher.setEmployeeNumber(employeeNumber);
        teacherRepository.save(teacher);

        String token = jwtUtil.generateToken(teacher.getEmail(), "TEACHER");

        return new AuthResponseDTO(token, teacher.getFirstName(), teacher.getLastName(), "TEACHER", employeeNumber);
    }

    @Transactional
    public AuthResponseDTO registerStudent(StudentRegisterDTO dto){

        if(teacherRepository.findByEmail(dto.getEmail()).isPresent() ||
                studentRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email already in use");
        }

        var institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));


        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setInstitution(institution);


        studentRepository.save(student);

        String studentNumber = String.format("EST-%03d", student.getId());
        student.setStudentNumber(studentNumber);
        studentRepository.save(student);

        String token = jwtUtil.generateToken(student.getEmail(), "STUDENT");

        return new AuthResponseDTO(token, student.getFirstName(), student.getLastName(), "STUDENT", studentNumber);
    }

    public AuthResponseDTO login(LoginRequestDTO dto){
        var teacher = teacherRepository.findByEmail(dto.getEmail());
        var student = studentRepository.findByEmail(dto.getEmail());

        if(teacher.isPresent()){
            if(passwordEncoder.matches(dto.getPassword(), teacher.get().getPassword())){
                String token = jwtUtil.generateToken(teacher.get().getEmail(), "TEACHER");
                return new AuthResponseDTO(token, teacher.get().getFirstName(), teacher.get().getLastName(), "TEACHER", teacher.get().getEmployeeNumber());
            }
        } else if(student.isPresent()){
            if(passwordEncoder.matches(dto.getPassword(), student.get().getPassword())){
                String token = jwtUtil.generateToken(student.get().getEmail(), "STUDENT");
                return new AuthResponseDTO(token, student.get().getFirstName(), student.get().getLastName(), "STUDENT", student.get().getStudentNumber());
            }
        }

        throw new RuntimeException("Invalid Credentials");
    }

    public AuthResponseDTO registerAdmin(AdminRegisterDTO dto){
        if(adminRepository.count()>0){
            throw new RuntimeException("An admin already exists");
        }
        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));
        if(adminRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email already in use");
        }
        Admin admin = new Admin();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setInstitution(institution);
        adminRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN");
        return new AuthResponseDTO(token, admin.getFirstName(), admin.getLastName(), "ADMIN", null);
    }
}
