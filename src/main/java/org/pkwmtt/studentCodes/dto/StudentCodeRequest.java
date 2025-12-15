package org.pkwmtt.studentCodes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCodeRequest {
    private String email;
    private String superiorGroupName;
    
    public String getMailMessage (String code) {
        return String.format(
          """
            <b>Kod grupy %s</b><br/>
            Twój kod: <b>%s</b> <br/><br/>
            Poniżej znajduje się kod służący do odblokowania możliwości dodawania/edytowanie/usuwania wydarzeń w kalendarzu dla twojej grupy. <br/>
            Wpisz kod w <b><i>[Ustawienia > Wpisz kod]</i></b> i przekaż go innym osobom.<br/>
            Twórcy aplikacji nie ponoszą odpowiedzialności za niewłaściwe użycie kodu przez osoby trzecie.<br/><br/>
            """, superiorGroupName, code
        );
    }
}
