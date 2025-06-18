package com.knowy.server.controller;

import com.knowy.server.controller.model.OptionQuizDTO;
import com.knowy.server.controller.model.QuestionDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class QuizController {

	public  QuizController() {

	}
	@GetMapping("/testQuestion")
	public String testQuestion(ModelMap model) {
		QuestionDTO questionDTO = new QuestionDTO("1","Question","images/knowylogo.png");
		model.addAttribute("questionNumber", questionDTO.getQuestionNumber());
		model.addAttribute("questionText", questionDTO.getQuestionText());
		model.addAttribute("imgPath", questionDTO.getImgPath());
		return "/pages/testQuestion";
	}

	//OptionsQuiz
	@GetMapping("/testOptionsQuiz")
	public String viewComponents(@RequestParam(defaultValue = "3") int quizID, ModelMap model) {
		//Instead of creating the repository and the service, I create the dummy data for later development....
		List<OptionQuizDTO> options = Arrays.asList(
			new OptionQuizDTO(1, 2, 3, "A.", "Esta respuesta es errónea.", false),
			new OptionQuizDTO(1, 2, 3, "B.", "Esta respuesta es muy buena gente pero falsa.", false),
			new OptionQuizDTO(1, 2, 3, "C.", "Esta respuesta mola cantidubi.", true),
			new OptionQuizDTO(1, 2, 3, "D.", "Esta respuesta te traerá dolores de cabeza.", false)
		);
		model.addAttribute("options", options);
		return "/pages/testOptionsQuiz";
	}
}
