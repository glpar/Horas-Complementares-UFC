import 'package:flutter/material.dart';

class InputQuestionsComponent extends StatefulWidget {
  List<Map<String, Object>> data;

  InputQuestionsComponent({super.key, required this.data});

  @override
  InputQuestionsComponentState createState() {
    return InputQuestionsComponentState(data);
  }
}

class InputQuestionsComponentState extends State<InputQuestionsComponent> {
  List<Map<String, Object>> data;
  int _cont = 0;
  int score0 = 0, score1 = 0, score2 = 0;

  InputQuestionsComponentState(this.data);

  final inputDecoration = InputDecoration(
      filled: true,
      fillColor: Colors.grey[300],
      border: OutlineInputBorder(
        borderRadius: BorderRadius.all(
          Radius.circular(10.0),
        ),
      ));

  TextEditingController textControllerQuestion = TextEditingController();
  TextEditingController textControllerItem1 = TextEditingController();
  TextEditingController textControllerItem2 = TextEditingController();
  TextEditingController textControllerItem3 = TextEditingController();

  @override
  void dispose() {
    super.dispose();
    textControllerQuestion.dispose();
    textControllerItem1.dispose();
    textControllerItem2.dispose();
    textControllerItem3.dispose();
  }

  bool formEmpty() {
    textControllerQuestion.text = textControllerQuestion.text.trim();
    textControllerItem1.text = textControllerItem1.text.trim();
    textControllerItem2.text = textControllerItem2.text.trim();
    textControllerItem3.text = textControllerItem3.text.trim();
    if (textControllerQuestion.text.isNotEmpty &&
        textControllerItem1.text.isNotEmpty &&
        textControllerItem2.text.isNotEmpty &&
        textControllerItem3.text.isNotEmpty &&
        (score0 == 1 || score1 == 1 || score2 == 1)) {
      return false;
    }
    return true;
  }

  void unCheckOthersAndCheckMe(int indexItem) {
    score0 = 0;
    score1 = 0;
    score2 = 0;
    switch (indexItem) {
      case 0:
        {
          score0 = 1;
          break;
        }
      case 1:
        {
          score1 = 1;
          break;
        }
      case 2:
        {
          score2 = 1;
          break;
        }
      default:
        {
          break;
        }
    }
  }

  bool thisItemChecked(int indexItem) {
    switch (indexItem) {
      case 0:
        {
          if (score0 == 1) return true;
          break;
        }
      case 1:
        {
          if (score1 == 1) return true;
          break;
        }
      case 2:
        {
          if (score2 == 1) return true;
          break;
        }
      default:
        {
          break;
        }
    }
    return false;
  }

  void addQuestion() {
    if (!formEmpty()) {
      List<Map<String, Object>> itens = [];

      Map<String, Object> auxMap1 = {};
      auxMap1.addAll({'text': textControllerItem1.text, 'score': score0});

      Map<String, Object> auxMap2 = {};
      auxMap2.addAll({'text': textControllerItem2.text, 'score': score1});

      Map<String, Object> auxMap3 = {};
      auxMap3.addAll({'text': textControllerItem3.text, 'score': score2});

      itens.add(auxMap1);
      itens.add(auxMap2);
      itens.add(auxMap3);

      Map<String, Object> auxMap = {};
      auxMap.addAll({'question': textControllerQuestion.text, 'itens': itens});

      data.add(auxMap);

      print(data);

      setState(() {
        _cont++;
        score0 = 0;
        score1 = 0;
        score2 = 0;

        textControllerQuestion.clear();
        textControllerItem1.clear();
        textControllerItem2.clear();
        textControllerItem3.clear();
      });
    } else {
      final snackBar = SnackBar(
        content: const Text('fill in all fields!'),
        action: SnackBarAction(
          label: 'ok',
          onPressed: () {},
        ),
      );
      ScaffoldMessenger.of(context).showSnackBar(snackBar);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('insert questions')),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.fromLTRB(25, 30, 25, 10),
            child: TextFormField(
              maxLength: 60,
              controller: textControllerQuestion,
              decoration: inputDecoration.copyWith(
                hintText: 'ex: whats is my favorite color?',
                labelText: 'question',
                floatingLabelBehavior: FloatingLabelBehavior.always,
              ),
              textInputAction: TextInputAction.next,
            ),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(25, 10, 25, 10),
            child: TextFormField(
                maxLength: 60,
                controller: textControllerItem1,
                decoration: inputDecoration.copyWith(
                  hintText: 'ex: black',
                  labelText: 'item 1',
                  floatingLabelBehavior: FloatingLabelBehavior.always,
                  suffixIcon: IconButton(
                    color: thisItemChecked(0) ? Colors.green : null,
                    onPressed: () => setState(() {
                      unCheckOthersAndCheckMe(0);
                    }),
                    icon: Icon(thisItemChecked(0)
                        ? Icons.check_circle
                        : Icons.circle_outlined),
                  ),
                ),
                textInputAction: TextInputAction.next),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(25, 10, 25, 10),
            child: TextFormField(
                maxLength: 60,
                controller: textControllerItem2,
                decoration: inputDecoration.copyWith(
                  hintText: 'ex: blue',
                  labelText: 'item 2',
                  floatingLabelBehavior: FloatingLabelBehavior.always,
                  suffixIcon: IconButton(
                    color: thisItemChecked(1) ? Colors.green : null,
                    onPressed: () => setState(() {
                      unCheckOthersAndCheckMe(1);
                    }),
                    icon: Icon(thisItemChecked(1)
                        ? Icons.check_circle
                        : Icons.circle_outlined),
                  ),
                ),
                textInputAction: TextInputAction.next),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(25, 10, 25, 10),
            child: TextFormField(
              maxLength: 60,
              controller: textControllerItem3,
              decoration: inputDecoration.copyWith(
                hintText: 'ex: purple',
                labelText: 'item 3',
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: IconButton(
                  color: thisItemChecked(2) ? Colors.green : null,
                  onPressed: () => setState(() {
                    unCheckOthersAndCheckMe(2);
                  }),
                  icon: Icon(thisItemChecked(2)
                      ? Icons.check_circle
                      : Icons.circle_outlined),
                ),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(25, 10, 25, 0),
            child: Container(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: addQuestion,
                child: Text('next question'),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(0, 50, 25, 0),
            child: Container(
              alignment: Alignment.centerRight,
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  int contAux = data.length;
                  addQuestion();
                  if (data.isNotEmpty) {
                    if (contAux != data.length) {
                      Navigator.pop(context, data);
                    }
                  } else {
                    final snackBar = SnackBar(
                      content: const Text('insert at least one question!'),
                      action: SnackBarAction(
                        label: 'ok',
                        onPressed: () {},
                      ),
                    );
                    ScaffoldMessenger.of(context).showSnackBar(snackBar);
                  }
                },
                child: Text('add question and conclude'),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(0, 10, 25, 0),
            child: Container(
              alignment: Alignment.centerRight,
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  if (data.isNotEmpty) {
                    Navigator.pop(context, data);
                  } else {
                    final snackBar = SnackBar(
                      content: const Text('insert at least one question!'),
                      action: SnackBarAction(
                        label: 'ok',
                        onPressed: () {},
                      ),
                    );
                    ScaffoldMessenger.of(context).showSnackBar(snackBar);
                  }
                },
                child: Text('just conclude'),
              ),
            ),
          )
        ],
      ),
    );
  }
}
