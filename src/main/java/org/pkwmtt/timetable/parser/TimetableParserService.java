package org.pkwmtt.timetable.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.calendar.exams.enums.SubjectType;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TimetableParserService {
    
    /**
     * Alters html code for it to fit parsing process
     *
     * @param html webpage content
     * @return altered html code
     */
    private String clean (String html) {
        return html.replaceAll("<br>", " ")
          .replaceAll(Pattern.quote("</span>-(N"), "-(N</span>")
          .replaceAll(Pattern.quote("</span>-(P"), "-(P</span>")
          .replaceAll(Pattern.quote("</span>-(p"), "-(p</span>")
          .replaceAll(Pattern.quote("</span>-(n"), "-(n</span>")
          .replaceAll(Pattern.quote("<span style=\"font-size:85%\">"), "")
          .replaceAll(Pattern.quote("<a"), "<span")
          .replaceAll(Pattern.quote("</a>"), "</span>")
          .replaceAll(Pattern.quote("J ang"), "J_ang")
          .replaceAll(Pattern.quote("J niemiecki"), "J_niemiecki")
          .replaceAll(Pattern.quote("WF hala ("), "WF_hala_(")
          .replaceAll(Pattern.quote("&nbsp;"), "");
        
    }
    
    /**
     * Extrude hours list from webpage
     *
     * @param html subpage of any general group
     * @return List of hours: <i>List of Strings</i>
     */
    public List<String> parseHours (String html) {
        //Parse html code to Document object (allows to query elements)
        Document document = Jsoup.parse(clean(html));
        
        List<String> hours = new ArrayList<>();
        
        for (Element item : document.select("td.g")) {
            hours.add(item.text());
        }
        
        return hours;
    }
    
    /**
     * Parse webpage html to map of general groups containing name of a group and link
     * to subpage with its timetable
     *
     * @param html .../list containing list of general groups
     * @return map of general groups in format [GroupName: URL]
     */
    public Map<String, String> parseGeneralGroups (String html) {
        Document document = Jsoup.parse(html);
        
        Map<String, String> generalGroups = new HashMap<>();
        
        for (Element item : document.select("#oddzialy .el a")) {
            generalGroups.put(item.text(), item.attr("href"));
        }
        
        return generalGroups;
    }
    
    /**
     * Parse html of specific General Group webpage to lists of subjects
     *
     * @param html of general group webpage
     * @return list of subjects sorted by day and odd or even type
     */
    public List<DayOfWeekDTO> parse (String html) {
        Document document = Jsoup.parse(clean(html));
        Elements rows = extractRows(document);
        
        List<DayOfWeekDTO> days = parseHeaders(rows);
        
        //Remove header row
        rows.removeFirst();
        
        //Go every row
        for (int rowId = 0; rowId < rows.size(); rowId++) {
            Element row = rows.get(rowId);
            Elements cell = row.select("td.l");
            
            //Go every cell in a row
            for (int columnId = 0; columnId < cell.size(); columnId++) {
                Elements items = getValidItems(cell.get(columnId));
                
                //Go every item in column
                for (int itemId = 0; itemId < items.size() - 1; itemId += 2) {
                    String name = items.get(itemId).text();
                    String classroom = items.get(itemId + 1).text();
                    
                    SubjectDTO subject = buildSubject(name, classroom, rowId);
                    
                    TypeOfWeek typeOfWeek;
                    
                   boolean notOdd = isNameNotOdd(name);
                   boolean notEven = isNameNotEven(name);
                   
                   if (notOdd == notEven) {
                       typeOfWeek = TypeOfWeek.BOTH;
                   } else if (notOdd) {
                       typeOfWeek = TypeOfWeek.EVEN;
                   } else {
                       typeOfWeek = TypeOfWeek.ODD;
                   }
                    
                    days.get(columnId).add(subject, typeOfWeek);
                }
            }
        }
        return days;
    }
    
    /**
     * Cleans names from unnecessary and unwanted characters
     *
     * @param rawName      subject name before cleaning process
     * @param rawClassroom classroom name before cleaning process
     * @param rowId        timetable row id
     * @return subject with cleaned data
     */
    private SubjectDTO buildSubject (String rawName, String rawClassroom, int rowId) {
        String name = cleanSubjectName(rawName);
        String classroom = cleanClassroomName(rawClassroom);
        SubjectType type = extractSubjectTypeFromName(name);
        
        return new SubjectDTO()
          .setName(name)
          .setClassroom(classroom)
          .setRowId(rowId)
          .setType(type);
    }
    
    /**
     * Finds items containing data in cell
     *
     * @param cell from timetable
     * @return items from cell
     */
    private Elements getValidItems (Element cell) {
        Elements items = cell.select("span");
        items.removeIf(item -> item.text().contains("#") || item.text().length() == 2);
        return items;
    }
    
    /**
     * Extracts subject type from its name
     *
     * @param name subject name
     * @return subject type or empty string if there isn't any specified
     */
    public static SubjectType extractSubjectTypeFromName (String name) {
        name = name.trim();
        if (name.endsWith("W")) {
            return SubjectType.LECTURE;
        }
        if (name.endsWith("S")) {
            return SubjectType.SEMINAR;
        }
        if (name.endsWith("Ć") || name.endsWith("ĆM") || name.endsWith("ĆK")) {
            return SubjectType.EXERCISES;
        }
        
        Pattern laboratoryPattern = Pattern.compile("(?<!#)L0[1-9]");
        Matcher labMatcher = laboratoryPattern.matcher(name);
        if (labMatcher.find()) {
            return SubjectType.LABORATORY;
        }
        
        Pattern computerLabPattern = Pattern.compile("(?<!#)K0[1-9]");
        Matcher compLabMatcher = computerLabPattern.matcher(name);
        if (compLabMatcher.find()) {
            return SubjectType.COMPUTER_LABORATORY;
        }
        
        Pattern projectPattern = Pattern.compile("(?<!#)P0[1-9]");
        Matcher projectMatcher = projectPattern.matcher(name);
        if (projectMatcher.find()) {
            return SubjectType.PROJECT;
        }
        
        return SubjectType.OTHER;
    }
    
    /**
     * Extracts rows with subjects from html
     *
     * @param document of general group html webpage
     * @return rows of timetable
     */
    private Elements extractRows (Document document) {
        Elements table = document.select("table");
        return table.select("table tbody tr td table tbody tr");
    }
    
    /**
     * Extracts headers from timetable
     *
     * @param rows of timetable
     * @return headers with days of week names
     */
    private List<DayOfWeekDTO> parseHeaders (Elements rows) {
        List<DayOfWeekDTO> days = new ArrayList<>();
        Elements headers = rows.getFirst().select("th");
        for (int i = 2; i < headers.size(); i++) {
            days.add(new DayOfWeekDTO(headers.get(i).text()));
        }
        return days;
    }
    
    private String cleanClassroomName (String text) {
        if (text.contains("-p")) {
            return text.replace("-p", "");
        }
        if (text.contains("-n")) {
            return text.replace("-n", "");
        }
        return text;
    }
    
    /**
     * Deletes all unnecessary characters in subject name
     *
     * @param text subject name
     * @return cleaned name
     */
    private String cleanSubjectName (String text) {
        text = text.replaceAll("-", "");
        text = deleteEvenMark(text);
        text = deleteOddMark(text);
        text = text.replaceAll(Pattern.quote(")"), "");
        text = text.replaceAll(Pattern.quote("."), "");
        return text;
    }
    
    /**
     * Deletes marks of odd day
     *
     * @param text subject name
     * @return altered text
     */
    private String deleteEvenMark (String text) {
        if (text.contains("(P")) {
            return text.replace("(P", "");
        }
        if (text.contains("(p")) {
            return text.replace("(p", "");
        }
        
        return text;
    }
    
    /**
     * Deletes marks of even day
     *
     * @param text subject name
     * @return altered text
     */
    private String deleteOddMark (String text) {
        
        text = text.replaceAll("-", "");
        
        if (text.contains("(N")) {
            return text.replace("(N", "");
        }
        if (text.contains("(n")) {
            return text.replace("(n", "");
        }
        return text;
    }
    
    /**
     * Checks if subjects name isn't odd
     *
     * @param name of a subject
     * @return true if subject isn't odd and
     * false if subject is odd
     */
    private boolean isNameNotOdd (String name) {
        return !name.contains("(N") && !name.contains("-(n");
    }
    
    private boolean isNameNotEven (String name) {
        return !name.contains("(P") && !name.contains("-(p");
    }
}