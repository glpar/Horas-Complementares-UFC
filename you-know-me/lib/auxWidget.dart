import 'package:flutter/material.dart';

class AuxWidget extends StatelessWidget {
  Function() callBack;

  AuxWidget(this.callBack);

  @override
  Widget build(Object context) {
    return Center(
      child: ElevatedButton(
        child: Text('create quiz'),
        onPressed: callBack,
      ),
    );
  }
}
