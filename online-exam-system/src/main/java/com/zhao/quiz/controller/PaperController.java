package com.zhao.quiz.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhao.quiz.domain.*;
import com.zhao.quiz.mapper.QuestionMapper;
import com.zhao.quiz.service.ClasseService;
import com.zhao.quiz.service.PaperService;
import com.zhao.quiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/paper")
public class PaperController {
    @Autowired
    private PaperService paperService;
    @Autowired
    private ClasseService classeService;
    @Autowired
    private QuestionService questionService;

    @RequestMapping("/getAllPaper")
    public String getAllPaper(Model model){
        List<Paper> papers = paperService.getAll();

        model.addAttribute("papers",papers);
        return "paper/paperList";
    }

    @RequestMapping("/toAddPaper")
    public String toAddPaper(){
        return "paper/paperAdd";
    }


    @RequestMapping("/addPaper")
    public String addPaper(Paper paper){
        paperService.addPaper(paper);
        return "redirect:/paper/getAllPaper";
    }


    @RequestMapping("/toEditPaper/{id}")
    public String toEditPaper(@PathVariable("id") Integer id,Model model){
        Paper paper=paperService.getPaperById(id);
        model.addAttribute("paper",paper);
        return "paper/paperEdit";
    }

    @RequestMapping("/EditPaper")
    public String toEditPaper(Paper paper){
        paperService.editPaper(paper);
        return "redirect:/paper/getAllPaper";
    }
    

    @RequestMapping("/deletePaper/{id}")
    public String deletePaperById(@PathVariable("id") Integer id,Model model){
        paperService.deletePaperById(id);
        return "redirect:/paper/getAllPaper";
    }


    @RequestMapping("/toManagerQuestion/{id}")
    public String toManagerQuestion(@PathVariable("id") Integer id,Model model){
        List<QuestionPaper> questionPapers = paperService.paperQueryALlQuestionById(id);
        model.addAttribute("papid",id);
        Paper paperName=paperService.queryPaperNameById(id);
        model.addAttribute("paperName",paperName.getPaperName());
        model.addAttribute("questionPapers",questionPapers);
        return "paper/ManagerQuestion";
    }

    @RequestMapping("/toAddQuestion/{id}")
    public String getAllQuestion(@PathVariable("id") Integer paperId,Question question,@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "4") int pageSize,
                                 Model model){

        List<Question> questionCourses=questionService.queryAllCourse();
        questionCourses.add(new Question("bug","all"));
        List<Question> questionTypes=questionService.queryAllType();
        questionTypes.add(new Question("k","bug"));
        String questionCourseBef = question.getQuestionCourse();
        String questionCourseresRes="";
        if(questionCourseBef==null){

            questionCourseresRes="all";
        }else {
            questionCourseresRes=questionCourseBef;
        }

        String questionTypeBef=question.getQuestionType();
        String questionTypesRes="";
        if(questionTypeBef==null){

            questionTypesRes="k";
        }else {
            questionTypesRes=questionTypeBef;
        }

        List<Question> questionids=paperService.queryALlQuestionIdInPaperById(paperId);
        List<Integer> quesds=new ArrayList<>();
        if(questionids!=null){
            for(Question qid:questionids){
                quesds.add(qid.getQuestionId());
            }
        }
        model.addAttribute("quesds",quesds);

        PageHelper.startPage(pageNum,pageSize);//这行是重点，表示从pageNum页开始，每页pageSize条数据
        List<Question> questions = questionService.getAll(question);
        PageInfo<Question> pageInfo = new PageInfo<Question>(questions);
        Paper paperName=paperService.queryPaperNameById(paperId);
        model.addAttribute("paperName",paperName.getPaperName());
        model.addAttribute("questionCourseresRes",questionCourseresRes);
        model.addAttribute("questionTypesRes",questionTypesRes);
        model.addAttribute("questionTypes",questionTypes);
        model.addAttribute("questionCourses",questionCourses);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("paperId",paperId);
        return "paper/AddQuestion";
    }

    @RequestMapping("/AddQuestion")
    public String AddQuestion(Integer paperId,Integer quesId,Integer pageNum,String questionCourse,String questionType){
        QuestionPaper questionPaper=new QuestionPaper(quesId,paperId);
        paperService.AddQuestionToPaperById(questionPaper);
        return "redirect:/paper/toAddQuestion/"+paperId+"?pageNum="+pageNum+"&questionCourse="+questionCourse+"&questionType="+questionType;
    }

    @RequestMapping("/detachQuestion")
    public String detachQuestion(Integer qpId,Integer paperId){
        paperService.detachQuestionById(qpId);
        return "redirect:/paper/toManagerQuestion/"+paperId;
    }


    @RequestMapping("/toRandomQuestion/{id}")
    public String toRandomQuestion(@PathVariable ("id") Integer papid,Question question,Model model){
        List<Question> questionCourses=questionService.queryAllCourse();
        questionCourses.add(new Question("bug","all"));

        int TotalQuestionNums=questionService.queryAllQuestionNums();
        List<Map> maps = questionService.queryNumOfQuestionType();
        List<String> course=new ArrayList<>();
        List<Integer> count=new ArrayList<>();
        for (Map map:maps){
            for (Object key : map.keySet()) {
                if(map.get(key) instanceof String){
                    course.add(map.get(key).toString());
                }else{
                    count.add(Integer.parseInt(map.get(key).toString()));
                }
            }
        }
        Paper paperName=paperService.queryPaperNameById(papid);
        model.addAttribute("paperName",paperName.getPaperName());
        model.addAttribute("count",count);
        model.addAttribute("course",course);
        model.addAttribute("TotalQuestionNums",TotalQuestionNums);
        model.addAttribute("paperId",papid);
        model.addAttribute("questionCourses",questionCourses);
        return "paper/RandomQuestion";
    }


    @RequestMapping("/RandomADDQuestion/{id}")
    public String RandomADDQuestion(@PathVariable ("id") Integer papid,String questionCourse,int QuesNums){

        PapIdQuesCourse papIdQuesCourse=new PapIdQuesCourse();
        papIdQuesCourse.setPapid(papid);
        papIdQuesCourse.setQuestionCourse(questionCourse);
        List<Question> questionsNodivIds=questionService.queryAllQueIdNotInPaperById(papIdQuesCourse);
        List<Integer> list=new ArrayList<>();
        for(Question question:questionsNodivIds){
            list.add(question.getQuestionId());
        }
        Collections.shuffle(list);
        List<QuestionPaper> questionPapers=new ArrayList<>();
        for(int i=0;i<QuesNums;i++){
            QuestionPaper questionPaper=new QuestionPaper(list.get(i),papid);
            questionPapers.add(questionPaper);
        }
        int nums=questionService.AddAllQues(questionPapers);
        return "redirect:/paper/toManagerQuestion/"+papid;
    }

    @RequestMapping("/deleteAllQues/{id}")
    public String deleteAllQues(@PathVariable ("id") Integer papid,Integer []ques){
        int nums=questionService.queryIdByPapQue(ques);
        return "redirect:/paper/toManagerQuestion/"+papid;
    }
}
