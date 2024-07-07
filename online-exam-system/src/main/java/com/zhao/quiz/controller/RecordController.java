package com.zhao.quiz.controller;

import com.zhao.quiz.domain.*;
import com.zhao.quiz.service.ClasseService;
import com.zhao.quiz.service.PaperService;
import com.zhao.quiz.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/record")
public class RecordController {
    @Autowired
    RecordService recordService;
    @Autowired
    PaperService paperService;
    @Autowired
    ClasseService classeService;


    @RequestMapping("/getAllRecord")
    public String getAllRecord(Model model){
        List<Record> records=recordService.queryAll();
        model.addAttribute("records",records);
        return "record/RecordList";
    }

    @RequestMapping("/deleteRecore/{id}")
    public String deleteRecore(@PathVariable ("id") Integer id){
        recordService.deleteById(id);
        return "redirect:/record/getAllRecord";
    }

    @RequestMapping("/toShowExamHist/{id}")
    public String toShowExamHist(@PathVariable ("id")Integer id,Model model){

        Integer papid=recordService.queryByRecordId(id);
        String answers=recordService.queryAnsByRecordId(id);

        List<QuestionPaper> questionPapers = paperService.paperQueryALlQuestionByIdOrderByType(papid);

        List<String> ans = Arrays.asList(answers.split(","));
        model.addAttribute("questionPapers",questionPapers);
        model.addAttribute("ans",ans);

        return "record/showExamHist";
    }


    @RequestMapping("/showClaAcc")
    public String showClaAcc(Model model){

        List<Record> records=recordService.queryAllExam();
                    List<ClaAcc> claAccRes=new ArrayList<>();

                    for(Record rec:records){

                        int paperid=rec.getPaperId();
                        int toscore=recordService.queryToscore(paperid);

                        String exaName=rec.getRecordName();
                        List<Classe> clas=recordService.queryAllClass(exaName);

                        int allScore=0;
                        int accScore=0;
                        for(Classe cla:clas){
                            int claId=cla.getClasseId();

                            Classe claName=classeService.queryClaNameById(claId);

                            if(claName != null){
                                RecordExam recordExam = new RecordExam();
                                recordExam.setClaId(claId);
                                recordExam.setExaName(exaName);
                                double setToscore = toscore * 0.6;
                                recordExam.setToscore(setToscore);

                                allScore = recordService.queryAllScore(recordExam);

                    accScore = recordService.queryAccScore(recordExam);
                    double accre = (double) accScore / allScore;

                    double acc = (double) Math.round(accre * 100) / 100;

                    ClaAcc claAcc = new ClaAcc();
                    claAcc.setExamName(exaName);
                    claAcc.setClaName(claName.getClasseName());
                    claAcc.setToscPer(allScore);
                    claAcc.setAcscPer(accScore);
                    claAcc.setAcc(acc);

                    claAccRes.add(claAcc);
                }else{
                    System.err.println("班级信息为 null，无法继续处理");
                }
            }
        }
        model.addAttribute("claAccRes",claAccRes);
        return "record/claAcc";
    }
}
