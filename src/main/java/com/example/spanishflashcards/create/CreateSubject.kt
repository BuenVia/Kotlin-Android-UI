package com.example.spanishflashcards.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.api.RetrofitInstance
import com.example.spanishflashcards.model.CreateSubject
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreateFunction(onBack: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val croutineScope = rememberCoroutineScope()

    var subjectVal by remember {
        mutableStateOf(CreateSubject(subjectName = ""))
    }

//    var vocabVal by remember {
//        mutableStateOf(CreateVocab(esp = "", eng = "", subjectId = 0L))
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Back button
        Button(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(25.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
        ) {
            Text("Back")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Create A Subject
            Text(text = "Create a Subject")
            TextField(
                value = subjectVal.subjectName,
                onValueChange = {
                    newText -> subjectVal = subjectVal.copy(subjectName = newText)
                },
                label = { Text(text = "Subject") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.padding(10.dp))

//            // Create the first vocab
//            Text("Create the firs Vocab")
//            // Vocab
//            TextField(
//                value = vocabVal.esp,
//                onValueChange = { vocabVal = vocabVal.copy(esp = it) },
//                label = { Text(text = "Vocab") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
//            )
//
//            Spacer(modifier = Modifier.padding(10.dp))
//
//            // Translation
//            TextField(
//                value = vocabVal.eng,
//                onValueChange = { vocabVal = vocabVal.copy(eng = it) },
//                label = { Text(text = "Translation") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
//            )
//
//            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
//                    croutineScope.launch { handleSubmit(subjectVal, vocabVal) }
                    croutineScope.launch { handleSubjectSubmit(subjectVal) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8EAF6),
                    contentColor = Color(0xFF3F51B5)
                )
            ) {
                Text(
                    text = "Create",
                    fontSize = 16.sp
                )
            }

        }

    }
}

suspend fun handleSubjectSubmit(
    subjectVal: CreateSubject,
//    vocabVal: CreateVocab
): String {


    // Logic for making the API call
    try {

        if (subjectVal.subjectName == "") {
            System.out.println("Empty Subject")
            return "Fail"
        }

        val createSubject = RetrofitInstance.api.createSubject(subjectVal)

        return "Success";

//        val vocabToCreate = vocabVal.copy(
//            subjectId = createSubject.subjectId
//        )
//
//        if (vocabToCreate.eng == "" || vocabToCreate.esp == "" || vocabToCreate.subjectId == null) {
//            System.out.println("Empty Vocab")
//            return
//        }
//
//        RetrofitInstance.api.createVocab(vocabToCreate)

    } catch (e: Exception) {
        e.printStackTrace()
        return "Fail";
    }

}