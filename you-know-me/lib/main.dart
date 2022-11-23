import 'package:exemplo/auxWidget.dart';
import 'package:exemplo/button.dart';
import 'package:exemplo/inputQuestionsComponent.dart';
import 'package:exemplo/question.dart';
import 'package:exemplo/quiz.dart';
import 'package:exemplo/result.dart';
import 'package:flutter/material.dart';
import 'package:exemplo/questionsAnswerWidget.dart';

main() {
  runApp(const MaterialApp(
    title: 'you know me?',
    home: YouKnowMe(),
  ));
}

class YouKnowMe extends StatefulWidget {
  const YouKnowMe({Key? key}) : super(key: key);

  @override
  _YouKnowMeState createState() {
    return _YouKnowMeState();
  }
}

class _YouKnowMeState extends State<YouKnowMe> {
  int _cont = 0;
  int _score = 0;
  String appBarTitle = 'you know me?';

  void _onRespond(int score) {
    setState(() {
      _score += score;
      _cont++;
      getAnswerSelected() ? null : appBarTitle = 'finish';
    });
  }

  void restartQuiz() {
    setState(() {
      _cont = 0;
      _score = 0;
      appBarTitle = 'you know me?';
    });
  }

  List<Map<String, Object>> questions = [];

  void questionsAnswer(BuildContext context) {}

  bool getAnswerSelected() {
    return _cont < questions.length;
  }

  //retorn true if user already include questions
  bool setQuestions() {
    return !(questions.length == 0);
  }

  Widget playGame(List<Widget> buttonsAnswers) {
    return getAnswerSelected()
        ? Quiz(
            Question(questions[_cont]['question'].toString()),
            buttonsAnswers,
          )
        : Result(
            'ok, you finished the quiz, and got ' +
                _score.toString() +
                ' questions right.',
            restartQuiz,
            () => Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => QuestionsAnswer(questions)),
            ),
          );
  }

  Future<void> _navigateCreateQuiz(BuildContext context) async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => InputQuestionsComponent(
                data: questions,
              )),
    );

    if (!mounted) return;

    questions = result;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    List<Map<String, Object>> answersList;
    getAnswerSelected()
        ? answersList = questions[_cont].cast()['itens']
        : answersList = [];

    List<Widget> buttonsAnswers = answersList
        .map((resp) => MyButton(resp['text'].toString(),
            () => {_onRespond(int.parse(resp['score'].toString()))}))
        .toList();

    // do {
    //   _navigateCreateQuiz(context);
    // } while (!setQuestions());
    return Scaffold(
        appBar: AppBar(title: Text(appBarTitle)),
        /*body: getAnswerSelected()
          ? Quiz(
              Question(questions[_cont]['text'].toString()),
              buttonsAnswers,
            )
          : Result(
              'ok, you finished the quiz, and got ' +
                  _score.toString() +
                  ' questions right.',
              restartQuiz,
              () => Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => QuestionsAnswer(questions)),
                  )),*/

        body: setQuestions()
            ? playGame(buttonsAnswers)
            : AuxWidget(() => _navigateCreateQuiz(context)));
  }
}
