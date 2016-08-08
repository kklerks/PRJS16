package ca.senecacollege.myvmlab.student.tabletopassistant;


import android.util.Log;

import net.sourceforge.jeval.Evaluator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;




public class CalculationsParser {

    private JSONObject sheet; //sheet belonging to the user
    private Calculation currentCalculation; //calculation currently trying to be executed
    private String[] currentCalcVars; //variable names used in the calculation
    private CalculationsParser[] currentCalcAffects; //sheets affected by the current calculation


    public CalculationsParser(String s) {

        currentCalculation = null;
        currentCalcAffects = null;

        try {
            sheet = new JSONObject(s);

        } catch (Exception e) {
            Log.e("Invalid sheet","",e);
        }
    }
    public CalculationsParser(JSONObject json) {
        this(json.toString()); //call other constructor
    }

    private class Calculation {
        public String name;
        public String calculationString;
        public String affects;
        public String targetVariable;
    }



//Largely useless; easier to search for blocks with ids instead of names
//    /*
//     * On success, returns the value of a block; -1 otherwise (e.g. value might have been a string)
//     */
//    public int getBlockValueByName(String blockName) {
//
//        double d = getBlockValueByNameDouble(blockName);
//        return (int) d;
//
//    }
//    /*
//     * Same as above but returns a double instead of int
//     */
//    public double getBlockValueByNameDouble(String blockName) {
//
//        double blockValue = 0;
//
//        try {
//            JSONObject blocks = sheet.getJSONObject("blocks");
//            JSONObject block = blocks.getJSONObject(blockName);
//            blockValue = block.getDouble("value");
//
//        } catch (Exception e) {
//            Log.e("getBlockValue","Nonexistent block name or block contains invalid value.",e);
//            //blockValue = Integer.parseInt(null); //will probably crash everything
//            blockValue = -1.1;
//        }
//
//        return blockValue;
//
//    }


    /*
     * On success, returns the value of a block; -1 otherwise (e.g. value might have been a string)
     */
    public int getBlockValueById(String blockId) {

        double d = getBlockValueByIdDouble(blockId);
        return (int) d;

    }
    /*
     * Same as above but returns a double instead of int
     */
    public double getBlockValueByIdDouble(String blockId) {

        double blockValue = 0;
        boolean found = false;

        try {
            JSONObject blocks = sheet.getJSONObject("blocks");

            Iterator<String> iterator = blocks.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject currBlock = blocks.getJSONObject(key);
                String html = currBlock.getString("html");
                int index = html.indexOf("id=\"");
                index += 4; //+4 offsets id="
                String idString = html.substring(index);
                //Log.d("html",idString);
                index = idString.indexOf("\"");
                idString = idString.substring(0,index);
                //Log.d("html",idString);

                if (blockId.equals(idString)) {
                    found = true;
                    blockValue = currBlock.getDouble("value");
                }
            }

            if (!found) throw new Exception("Could not find block with id of " + blockId);

        } catch (Exception e) {
            Log.e("getBlockValue","Nonexistent block id or block contains invalid value.",e);
            //blockValue = Integer.parseInt(null); //will probably crash everything
            blockValue = -1.1;
        }

        return blockValue;

    }

//Largely useless; easier to search for blocks with ids instead of names
//
//    /*
//     * On success, sets block with value if found and returns true; false otherwise
//     */
//    public boolean setBlockValueByName(String blockName, int value) {
//
//        return setBlockValueByName(blockName, (double) value);
//
//    }
//    /*
//     * Same as above but accepts a double instead of an int
//     */
//    public boolean setBlockValueByName(String blockName, double value) {
//        boolean status = true;
//
//        try {
//            JSONObject newSheet = new JSONObject(sheet.toString());
//            JSONObject blocks = newSheet.getJSONObject("blocks");
//            JSONObject block = blocks.getJSONObject(blockName);
//            block.put("value",value);
//
//            sheet = newSheet;
//
//        } catch (Exception e) {
//            Log.e("setBlockValue","Nonexistent block name or attempting to set invalid value.",e);
//            status = false;
//        }
//
//        return status;
//    }

    /*
     * On success, sets block with value if found and returns true; false otherwise
     */
    public boolean setBlockValueById(String blockId, int value) {

        return setBlockValueById(blockId, (double) value);

    }
    /*
     * Same as above but accepts a double instead of an int
     */
    public boolean setBlockValueById(String blockId, double value) {
        boolean status = true;
        boolean found = false;

        try {
            JSONObject newSheet = new JSONObject(sheet.toString());
            JSONObject blocks = newSheet.getJSONObject("blocks");

            Iterator<String> iterator = blocks.keys();

            //iterate through each block to compare its ids
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject currBlock = blocks.getJSONObject(key);
                String html = currBlock.getString("html");
                int index = html.indexOf("id=\"");
                index += 4; //+4 offsets id="
                String idString = html.substring(index);
                //Log.d("html",idString);
                index = idString.indexOf("\"");
                idString = idString.substring(0,index);
                //Log.d("html",idString);

                if (blockId.equals(idString)) {
                    found = true;
                    String newHtml = html;
                    //Log.d("   html",newHtml);

                    //get old value as string so it can be replaced
                    index = html.indexOf("resourcevalue=\"");
                    index += 15; //+4 offsets resourcevalue="
                    String oldValue = html.substring(index);
                    index = oldValue.indexOf("\"");
                    oldValue = oldValue.substring(0,index);

                    //replace block value and html
                    if ((value % 1) == 0) {
                        newHtml = newHtml.replace("resourcevalue=\"" + oldValue + "\"", "resourcevalue=\"" + Integer.toString((int)value) + "\"");
                        currBlock.put("html",newHtml);
                        currBlock.put("value",Integer.toString((int)value));
                    } else {
                        newHtml = newHtml.replace("resourcevalue=\"" + oldValue + "\"", "resourcevalue=\"" + Double.toString(value) + "\"");
                        currBlock.put("html",newHtml);
                        currBlock.put("value",Double.toString(value));
                    }

                    //Log.d("newHtml",newHtml);
                }
            }

            if (!found) throw new Exception("Could not find block with id of " + blockId);

            sheet = newSheet;

        } catch (Exception e) {
            Log.e("setBlockValue","Nonexistent block name or attempting to set invalid value.",e);
            status = false;
        }

        return status;
    }


    public JSONObject getCurrentSheetJSON() {
        return sheet;
    }

    public String getCurrentSheet() {
        return sheet.toString();
    }




    /*
     * Accepts the calculation name and the sheets expected to be affected by the calculation
     * and tries to execute the calculation. Returns true on success; false otherwise.
     */
    public boolean parseCalculationByName(String calcName, CalculationsParser... affected) {

        //save list of sheets that are affected by the calculation
        currentCalcAffects = null; //remove existing sheets
        ArrayList<CalculationsParser> al = new ArrayList<CalculationsParser>();


        for (int i = 0; i < affected.length; i++) {
            if (affected[i] != this) al.add(affected[i]);
        }
        currentCalcAffects = new CalculationsParser[affected.length];
        currentCalcAffects = al.toArray(currentCalcAffects);

        boolean status;

        //find the calculation
        status = findCalculationByName(calcName);

        //extract calculation variables
        if (status) {
            status = extractCalculationVars();
        }

        //create, eval then execute the calculation
        if (status) {
            status = buildAndExecuteCalculation();
        }

        return status;

    }

    /*
     * Try to find the calculation in the sheet and if found, set the calculation as the current one
     */
    private boolean findCalculationByName(String calcName) {

        boolean found = false;
        Calculation calculation = new Calculation();

        try {

            JSONObject calculations = sheet.getJSONObject("calculations");
            Iterator<String> iterator = calculations.keys();

            while (iterator.hasNext()) {

                String key = iterator.next();

                JSONObject currCalculation = calculations.getJSONObject(key);
                String name = currCalculation.getString("name");

                if (name.equals(calcName)) {
                    //calculation found; save it's variables

                    found = true;

                    calculation.name = currCalculation.getString("name");
                    calculation.calculationString = currCalculation.getString("value");
                    calculation.affects = currCalculation.getString("targetPlayer");
                    calculation.targetVariable = currCalculation.getString("targetVar");
                }

            }

            //if (!found) calculation = null;
            if (!found) {
                throw new Exception("Nonexistent calculation name or sheet has no calculations.");
            } else {
                currentCalculation = calculation;
            }
        } catch (Exception e) {
            Log.e("parseCalculationByName", "", e);
            //calculation = null;
        }

        return found;
    }

    /*
     * Sets some information about the current calculation
     */
    private boolean extractCalculationVars() {

        boolean status = true;
        boolean needsOwnVariable = false;
        boolean needsOtherPlayersVariable = false;


        //attempt to extract variables from calculation string
        String tempCalcString = currentCalculation.calculationString;
        //Log.d("tempCalcString",tempCalcString);

        int selfVar = 0;
        int otherVar = 0;
        int endVar = 1;

        ArrayList<String> al = new ArrayList<String>();


        while (endVar != -1) {
            //messy loop to extract variables; adds a '@' at the front of variables for other players

            String temp;

            selfVar = tempCalcString.indexOf('[');
            otherVar = tempCalcString.indexOf('@');
            endVar = tempCalcString.indexOf(']');
            //Log.d("soe","self:" + selfVar + " otherVar:" + otherVar + " endVar:" + endVar);

            if (((selfVar < otherVar)&&(selfVar != -1))||((selfVar > -1)&&(otherVar == -1))) {

                temp = tempCalcString.substring(selfVar+1,endVar); //+1 offsets [
                //Log.d("EXTRACTED",temp);
                al.add(temp);
                tempCalcString = tempCalcString.substring(endVar+1);
                //Log.d("NewTemp",tempCalcString);

            } else if (((otherVar < selfVar)&&(otherVar != -1))||((otherVar > -1)&&(selfVar == -1))) {

                temp = tempCalcString.substring(otherVar+2,endVar); //+2 offsets @[
                //Log.d("EXTRACTED (@) ",temp);
                al.add("@" + temp); //add '@' at front for variable affecting another sheet
                tempCalcString = tempCalcString.substring(endVar+1);
                //Log.d("NewTemp (@)",tempCalcString);

            } else {
                endVar = -1; //exit the loop
            }

        }

        //save the names of variables used in the calculation
        if (!al.isEmpty()) {

            currentCalcVars = al.toArray(new String[0]);

        } else {
            currentCalcVars = null;
        }

        return status;

    }

    /*
     * Replace the variables in the calculation string with the actual values and attempts to execute them
     */
    private boolean buildAndExecuteCalculation() {
        boolean status = true;


        try {
            String newCalculationString = this.currentCalculation.calculationString;

            if (!currentCalculation.affects.equals("manyPlayers")) {
                //calculation only affects self or one other player

                CalculationsParser otherSheet;

                if (currentCalcAffects.length == 0) {
                    otherSheet = this;
                    Log.w("build+executeCalc","No sheet was passed to parseCalculation. Calculation assumed to only affect own sheet. This should be an error if the calculation requires another player's variables.");
                } else {
                    otherSheet = currentCalcAffects[0]; //sheet used for other player's variables
                }

                if (currentCalcVars != null) { //handle sheets without calculations

                    //for loop to go through each calculation variable
                    for (int i = 0; i < currentCalcVars.length; i++) {

                        String currCalcId = currentCalcVars[i];

                        //int currVarValue;
                        double currVarValue;


                        if (currCalcId.charAt(0) != '@') {
                            //use variable from another sheet

                            //Log.d("currCalcId",currCalcId);
                            currVarValue = this.getBlockValueById(currCalcId);
                            String valueAsString = String.valueOf(currVarValue);
                            newCalculationString = newCalculationString.replace("[" + currCalcId + "]", valueAsString);
                            //Log.d("newCalculationString",newCalculationString);

                        } else {
                            //use variable from this sheet

                            currCalcId = currCalcId.substring(1); //remove the prepended '@'
                            //Log.d("currCalcId",currCalcId);
                            currVarValue = otherSheet.getBlockValueById(currCalcId);
                            String valueAsString = String.valueOf(currVarValue);
                            newCalculationString = newCalculationString.replace("@[" + currCalcId + "]", valueAsString);
                            //Log.d("newCalculationString",newCalculationString);
                        }
                    }
                }

                if (currentCalculation.affects.equals("self")) {

                    status = eval(newCalculationString, this);

                } else if (currentCalculation.affects.equals("player")) {

                    status = eval(newCalculationString, otherSheet);

                } else {
                    Log.wtf("build+executeCalc", "Unknown target player. Value was neither 'self' or 'player' (was: " + currentCalculation.affects + ")");
                }


            } else {

                //for loop to go through each affected sheet
                for (int i = 0; i < currentCalcAffects.length; i++) {

                    CalculationsParser currentOtherSheet = currentCalcAffects[i];

                    if (currentCalcVars != null) { //handle sheets without calculations

                        //for loop to go through each calculation variable
                        for (int j = 0; j < currentCalcVars.length; j++) {

                            String currCalcId = currentCalcVars[j];

                            //int currVarValue;
                            double currVarValue;


                            if (currCalcId.charAt(0) != '@') {
                                //use variable from another sheet

                                //Log.d("currCalcId",currCalcId);
                                currVarValue = this.getBlockValueById(currCalcId);
                                String valueAsString = String.valueOf(currVarValue);
                                newCalculationString = newCalculationString.replace("[" + currCalcId + "]", valueAsString);
                                //Log.d("newCalculationString",newCalculationString);

                            } else {
                                //use variable from this sheet

                                currCalcId = currCalcId.substring(1); //remove the prepended '@'
                                //Log.d("currCalcId",currCalcId);
                                currVarValue = currentOtherSheet.getBlockValueById(currCalcId);
                                String valueAsString = String.valueOf(currVarValue);
                                newCalculationString = newCalculationString.replace("@[" + currCalcId + "]", valueAsString);
                                //Log.d("newCalculationString",newCalculationString);
                            }
                        }
                    }

                    status = eval(newCalculationString, currentOtherSheet);

                }
            }
        } catch (Exception e) {

            Log.e("buildCalc","",e);
            status = false;

        }

        return status;
    }

    /*
     * Evaluate the passed string by changing all appropriate values
     * Uses the external library JEval
     */
    private boolean eval(String evalString, CalculationsParser targetSheet) {
        boolean status = true;

        Log.d("eval Goal"," (" + currentCalculation.affects + "'s)" + currentCalculation.targetVariable + " = " + evalString + ";");

        try {

            Evaluator evaluator = new Evaluator();

            String result = evaluator.evaluate(evalString);

            double resultDouble = Double.parseDouble(result);
            int resultInteger;

            if ((resultDouble % 1) == 0) {
                //result can be accepted as an int
                resultInteger = Integer.parseInt(result.substring(0, result.indexOf('.')));

                if (currentCalculation.affects.equals("self")) {
                    this.setBlockValueById(currentCalculation.targetVariable,resultInteger);
                } else if (currentCalculation.affects.equals("player")) {
                    targetSheet.setBlockValueById(currentCalculation.targetVariable, resultInteger);
                } else if (currentCalculation.affects.equals("manyPlayers")) {
                    targetSheet.setBlockValueById(currentCalculation.targetVariable, resultInteger);
                } else {
                    Log.wtf("eval", "Unknown target player. Value was neither 'self' or 'player' (was: " + currentCalculation.affects + ")");
                    status = false;
                }
            } else {
                //result was a double

                if (currentCalculation.affects.equals("self")) {
                    this.setBlockValueById(currentCalculation.targetVariable,resultDouble);
                } else if (currentCalculation.affects.equals("player")) {
                    targetSheet.setBlockValueById(currentCalculation.targetVariable, resultDouble);
                } else if (currentCalculation.affects.equals("manyPlayers")) {
                    targetSheet.setBlockValueById(currentCalculation.targetVariable,resultDouble);
                } else {
                    Log.wtf("eval", "Unknown target player. Value was neither 'self' or 'player' (was: " + currentCalculation.affects + ")");
                    status = false;
                }
            }

        } catch (Exception e) {
            Log.e("Evaluator","",e);
            status = false;
        }

        return status;
    }

}
