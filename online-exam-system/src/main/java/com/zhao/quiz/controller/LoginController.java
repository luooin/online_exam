package com.zhao.quiz.controller;

import com.zhao.quiz.domain.*;
import com.zhao.quiz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private ClasseService classeService;
    @Autowired
    private RecordService recordService;
    @RequestMapping("/")
    public String view(Model model){

        int teas=teacherService.queryCountAll();
        int stus=studentService.queryCOuntALlstu();
        int alllogers=teas+stus;

        int allQues=questionService.queryCountAllQues();

        int allPaps=paperService.queryCountALlPaps();
        model.addAttribute("allPaps",allPaps);
        model.addAttribute("allQues",allQues);
        model.addAttribute("alllogers",alllogers);
        return "stage/prexam";
    }

    @RequestMapping("/foreLogin")
    public String foreLogin(){
        return "stage/login";
    }

    @RequestMapping("/backLogin")
    public String backLogin(){
        return "stage/loginx";
    }


    @ResponseBody
    @RequestMapping("/backLogin/check")
    public Object backCheck(Teacher teacher, HttpServletRequest request){
        AjaxResult result=new AjaxResult();
        HttpSession session=request.getSession();
        Teacher teac=teacherService.check(teacher);
        if(teac!=null){
            session.setAttribute("logerd",teac);
            result.setSuccess(true);
        }else {
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping("/index")
    public String index(Model model){

        int teas=teacherService.queryCountAll();
        int stus=studentService.queryCOuntALlstu();
        int alllogers=teas+stus;

        int allQues=questionService.queryCountAllQues();

        int allPaps=paperService.queryCountALlPaps();
        List<Record> ScoreHStu=recordService.queryRankScoreRecord();
        List<Record> AccHStu=recordService.queryRankAccRecord();
        model.addAttribute("ScoreHStu",ScoreHStu);
        model.addAttribute("AccHStu",AccHStu);
        model.addAttribute("allPaps",allPaps);
        model.addAttribute("allQues",allQues);
        model.addAttribute("alllogers",alllogers);
        return "index";
    }


    @ResponseBody
    @RequestMapping("/foreCheck/check")
    public Object foreCheck(Student student, HttpServletRequest request){
        AjaxResult result=new AjaxResult();
        HttpSession session=request.getSession();
        Student stud=studentService.check(student);
        if(stud!=null){
            session.setAttribute("loger",stud);
            result.setSuccess(true);
        }else {
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping("/indexprexam")
    public String indexprexam(){
        return "stage/prexamed";
    }


    @RequestMapping(value = {"*/logout","/logout","teacher/logout"})
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }


    @RequestMapping("/prexam/toAddStudent")
    public String toAddStudent(Model model){
        List<Classe> allClasees = classeService.getAll();
        model.addAttribute("allClasees",allClasees);
        return "stage/studentAdd";
    }

    @RequestMapping("/prexam/AddStudent")
    public String AddStudent(Student student){
        studentService.AddStudent(student);
        return "redirect:/foreLogin";
    }
    @RequestMapping("/zhao")
    public String zhao(){
        return "stage/zhao";
    }
}
