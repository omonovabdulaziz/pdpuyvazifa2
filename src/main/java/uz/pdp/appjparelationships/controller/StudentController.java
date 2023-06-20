package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDTO;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;

    //CREATE
    @PostMapping
    public String addStudent(@RequestBody StudentDTO studentDTO) {
        Address address = new Address();
        address.setStreet(studentDTO.getStreet());
        address.setDistrict(studentDTO.getDistrict());
        address.setCity(studentDTO.getCity());
        Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());
        if (!optionalGroup.isPresent())
            return "group id topilmadi";
        Group group = optionalGroup.get();
        Student student = new Student();
        student.setAddress(address);
        student.setGroup(group);
        student.setLastName(studentDTO.getLastName());
        student.setFirstName(studentDTO.getFirstName());
        studentRepository.save(student);
        return "saved";
    }

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentByFaculty(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }
    //4. GROUP OWNER

    @GetMapping("/groupId/{groupid}")
    public Page<Student> getStudentpage(@PathVariable Integer groupid, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupid, pageable);
    }
    //UPDATE
    @PutMapping("/forUpdate/{studentId}")
    public String updateStudent(@PathVariable Integer studentId , @RequestBody StudentDTO studentDTO ){
        Optional<Group> optionalGroup = groupRepository.findById(studentDTO.getGroupId());
        if (!optionalGroup.isPresent())
            return "bunday id li group topilmadi";

        Address address = new Address();
        address.setCity(studentDTO.getCity());
        address.setDistrict(studentDTO.getDistrict());
        address.setStreet(studentDTO.getStreet());
        Student student = new Student();
        student.setId(studentId);
        student.setAddress(address);
        student.setGroup(optionalGroup.get());
        student.setFirstName(student.getFirstName());
        student.setLastName(student.getLastName());
        studentRepository.save(student);
        return "student updated";
    }

    //DELETE
    @DeleteMapping("/forDelete/{studentId}")
    public String deleteStudent(@PathVariable Integer studentId){
        try {
            studentRepository.deleteById(studentId);
            return "student deleted";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }

    }


}
