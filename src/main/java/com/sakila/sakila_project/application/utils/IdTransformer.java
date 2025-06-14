package com.sakila.sakila_project.application.utils;

import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;

import java.util.Base64;

public class IdTransformer {
    private static final String dividingChar = ":";
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    public static String transform(String paddingContent, int id){
        var value = paddingContent + dividingChar + Integer.toString(id);
        var valueBytes = value.getBytes();
        return encoder.encodeToString(valueBytes);
    }

    public static Result<Integer> obtainId(String content){
        if(content.isBlank()){
            return Result.Failed(new Error("The id are empty", ErrorType.OPERATION_ERROR));
        }

        byte[] bytes;
        try{
            bytes = decoder.decode(content);
        }
        catch (IllegalArgumentException e){
            return Result.Failed(new Error("the id are not exist", ErrorType.OPERATION_ERROR));
        }

        var valueArray = new String(bytes).split(dividingChar);

        if(valueArray.length < 2){
            return Result.Failed(new Error("The id format are invalid", ErrorType.OPERATION_ERROR));
        }

        var idString = valueArray[valueArray.length - 1];
        int id;

        try{
            id = Integer.parseInt(idString);
        }
        catch (NumberFormatException e){
            return Result.Failed(new Error("The id provided aren't number", ErrorType.OPERATION_ERROR));
        }

        return Result.Success(id);
    }
}
