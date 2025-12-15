package org.pkwmtt;

public interface ValuesForTest {

    String timetableHTML = """
            <html>
                   <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                    <meta http-equiv="Content-Language" content="pl">
                    <meta name="description" content="Wydział Mechaniczny Politechniki Krakowskiej. /nPlan lekcji oddziału 12K1/n utworzony za pomocą programu Plan lekcji Optivum firmy VULCAN">
                    <title>Plan lekcji oddziału - 12K1</title>
                    <link rel="stylesheet" href="../css/plan.css" type="text/css">
                    <script language="JavaScript1.2" type="text/javascript" src="../scripts/plan.js"></script>
                   </head>
                   <body>
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="tabtytul">
                     <tbody>
                      <tr>
                       <td class="tytul"><img src="../images/pusty.gif" height="80" width="1"> <span class="tytulnapis">12K1</span></td>
                      </tr>
                     </tbody>
                    </table>
                    <div align="center">
                     <table border="0" cellpadding="10" cellspacing="0">
                      <tbody>
                       <tr>
                        <td colspan="2">
                         <table border="1" cellspacing="0" cellpadding="4" class="tabela">
                          <tbody>
                           <tr>
                            <th>Nr</th>
                            <th>Godz</th>
                            <th>Poniedziałek</th>
                            <th>Wtorek</th>
                            <th>Środa</th>
                            <th>Czwartek</th>
                            <th>Piątek</th>
                           </tr>
                           <tr>
                            <td class="nr">1</td>
                            <td class="g">7:30- 8:15</td>
                            <td class="l"><span class="p">PInterfUż W</span>-(N) <span class="p">#PIU</span> <a href="s204.html" class="s">J207.1-n</a><br><span class="p">Proj3D W</span>-(P) <span class="p">#3-D</span> <a href="s205.html" class="s">J207.1-p</a></td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">2</td>
                            <td class="g">8:15- 9:00</td>
                            <td class="l"><span class="p">PInterfUż W</span>-(N) <span class="p">#PIU</span> <a href="s204.html" class="s">J207.1-n</a><br><span class="p">Proj3D W</span>-(P) <span class="p">#3-D</span> <a href="s205.html" class="s">J207.1-p</a></td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">GodzPrzem</span>-(P) <span class="p">#GdP</span> <a href="s25.html" class="s">C04-p</a></td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">3</td>
                            <td class="g">9:15-10:00</td>
                            <td class="l"><span class="p">Mechatro P04</span>-(n. <span class="p">#Mtr</span> <a href="s113.html" class="s">K227-n</a><br><span class="p">Proj3D K04</span>-(p. <span class="p">#3D</span> <a href="s205.html" class="s">J207.1-p</a><br><span style="font-size:85%"><span class="p">Proj3D K01-(n.</span> <a href="n87.html" class="n">Do</a> <a href="s204.html" class="s">J207.1-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L02-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L02-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span><br><span style="font-size:85%"><span class="p">Mechatro L01-(P.</span> <a href="n380.html" class="n">S!</a> <a href="s116.html" class="s">K228-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PInterUż K01-(p.</span> <a href="n88.html" class="n">_D</a> <a href="s205.html" class="s">J207.1-p</a></span></td>
                            <td class="l"><span class="p">BazDan K04</span>-(n. <span class="p">#Bda</span> <a href="s194.html" class="s">G117-n</a><br><span class="p">BazDan K04</span>-(p. <span class="p">#bdA</span> <a href="s195.html" class="s">G117-p</a><br><span style="font-size:85%"><span class="p">PKM K01-(n.</span> <a href="n49.html" class="n">KB</a> <a href="s70.html" class="s">A227-n</a></span><br><span style="font-size:85%"><span class="p">WspInfPM P01-(p.</span> <a href="n72.html" class="n">CS</a> <a href="s244.html" class="s">A338-p</a></span></td>
                            <td class="l"><span class="p">Inżopr W</span>-(N) <span class="p">#IOP</span> <a href="s2.html" class="s">A123-n</a><br><span class="p">Inżopr W</span>-(P) <span class="p">#IOp</span> <a href="s3.html" class="s">A123-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">4</td>
                            <td class="g">10:00-10:45</td>
                            <td class="l"><span class="p">Mechatro P04</span>-(n. <span class="p">#Mtr</span> <a href="s113.html" class="s">K227-n</a><br><span class="p">Proj3D K04</span>-(p. <span class="p">#3D</span> <a href="s205.html" class="s">J207.1-p</a><br><span style="font-size:85%"><span class="p">Proj3D K01-(n.</span> <a href="n87.html" class="n">Do</a> <a href="s204.html" class="s">J207.1-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L02-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L02-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span><br><span style="font-size:85%"><span class="p">Mechatro L01-(P.</span> <a href="n380.html" class="n">S!</a> <a href="s116.html" class="s">K228-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PInterUż K01-(p.</span> <a href="n88.html" class="n">_D</a> <a href="s205.html" class="s">J207.1-p</a></span></td>
                            <td class="l"><span class="p">BazDan K04</span>-(n. <span class="p">#Bda</span> <a href="s194.html" class="s">G117-n</a><br><span class="p">BazDan K04</span>-(p. <span class="p">#bdA</span> <a href="s195.html" class="s">G117-p</a><br><span style="font-size:85%"><span class="p">PKM K01-(n.</span> <a href="n49.html" class="n">KB</a> <a href="s70.html" class="s">A227-n</a></span><br><span style="font-size:85%"><span class="p">WspInfPM P01-(p.</span> <a href="n72.html" class="n">CS</a> <a href="s244.html" class="s">A338-p</a></span></td>
                            <td class="l"><span class="p">Inżopr W</span>-(N) <span class="p">#IOP</span> <a href="s2.html" class="s">A123-n</a><br><span class="p">Inżopr W</span>-(P) <span class="p">#IOp</span> <a href="s3.html" class="s">A123-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">5</td>
                            <td class="g">11:00-11:45</td>
                            <td class="l"><span class="p">PAplikIn K04</span>-(n. <span class="p">#pai</span> <a href="s206.html" class="s">J209-n</a><br><span class="p">PInterUż K04</span>-(p. <span class="p">#piu</span> <a href="s205.html" class="s">J207.1-p</a><br><span style="font-size:85%"><span class="p">Mechatro P01-(n.</span> <a href="n433.html" class="n">TJ</a> <a href="s113.html" class="s">K227-n</a></span><br><span style="font-size:85%"><span class="p">PAplikIn K01-(p.</span> <a href="n450.html" class="n">_W</a> <a href="s207.html" class="s">J209-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L02-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L02-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">BazDan K04</span>-(n. <span class="p">#Bda</span> <a href="s194.html" class="s">G117-n</a><br><span class="p">BazDan K04</span>-(p. <span class="p">#bdA</span> <a href="s195.html" class="s">G117-p</a></td>
                            <td class="l"><span class="p">PKM W</span>-(P) <span class="p">#PKM</span> <a href="s5.html" class="s">A124-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">6</td>
                            <td class="g">11:45-12:30</td>
                            <td class="l"><span class="p">PAplikIn K04</span>-(n. <span class="p">#pai</span> <a href="s206.html" class="s">J209-n</a><br><span class="p">PInterUż K04</span>-(p. <span class="p">#piu</span> <a href="s205.html" class="s">J207.1-p</a><br><span style="font-size:85%"><span class="p">Mechatro P01-(n.</span> <a href="n433.html" class="n">TJ</a> <a href="s113.html" class="s">K227-n</a></span><br><span style="font-size:85%"><span class="p">PAplikIn K01-(p.</span> <a href="n450.html" class="n">_W</a> <a href="s207.html" class="s">J209-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L01-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L01-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">Inżopr P01-(n.</span> <a href="n119.html" class="n">Gj</a> <a href="s198.html" class="s">G120-n</a></span><br><span style="font-size:85%"><span class="p">Inżopr K01-(p.</span> <a href="n120.html" class="n">gJ</a> <a href="s199.html" class="s">G120-p</a></span></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">PKM W</span>-(P) <span class="p">#PKM</span> <a href="s5.html" class="s">A124-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">7</td>
                            <td class="g">12:45-13:30</td>
                            <td class="l"><span style="font-size:85%"><span class="p">PrSteroP L01-(n.</span> <a href="n355.html" class="n">SP</a> <a href="s184.html" class="s">G107-n</a></span><br><span style="font-size:85%"><span class="p">PrSteroP L01-(p.</span> <a href="n356.html" class="n">sp</a> <a href="s185.html" class="s">G107-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L01-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L01-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span><br><span style="font-size:85%"><span class="p">Mechatro L02-(N.</span> <a href="n379.html" class="n">Só</a> <a href="s115.html" class="s">K228-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">Inżopr P01-(n.</span> <a href="n119.html" class="n">Gj</a> <a href="s198.html" class="s">G120-n</a></span><br><span style="font-size:85%"><span class="p">Inżopr K01-(p.</span> <a href="n120.html" class="n">gJ</a> <a href="s199.html" class="s">G120-p</a></span></td>
                            <td class="l"><span class="p">Inżopr P04</span>-(n. <span class="p">#ioP</span> <a href="s198.html" class="s">G120-n</a><br><span class="p">Inżopr K04</span>-(p. <span class="p">#iop</span> <a href="s199.html" class="s">G120-p</a></td>
                            <td class="l"><span class="p">WspInfPM W</span>-(N) <span class="p">#WPM</span> <a href="s4.html" class="s">A124-n</a><br><span class="p">Mechatron W</span>-(P) <span class="p">#MTR</span> <a href="s27.html" class="s">G18-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">8</td>
                            <td class="g">13:30-14:15</td>
                            <td class="l"><span style="font-size:85%"><span class="p">PrSteroP L01-(n.</span> <a href="n355.html" class="n">SP</a> <a href="s184.html" class="s">G107-n</a></span><br><span style="font-size:85%"><span class="p">PrSteroP L01-(p.</span> <a href="n356.html" class="n">sp</a> <a href="s185.html" class="s">G107-p</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PSieciKP L01-(N.</span> <a href="n347.html" class="n">AP</a> <a href="s190.html" class="s">G110-n</a></span><br><span style="font-size:85%"><span class="p">PSieciKP L01-(P.</span> <a href="n348.html" class="n">PA</a> <a href="s191.html" class="s">G110-p</a></span><br><span style="font-size:85%"><span class="p">Mechatro L02-(N.</span> <a href="n379.html" class="n">Só</a> <a href="s115.html" class="s">K228-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">Inżopr P01-(n.</span> <a href="n119.html" class="n">Gj</a> <a href="s198.html" class="s">G120-n</a></span><br><span style="font-size:85%"><span class="p">Inżopr K01-(p.</span> <a href="n120.html" class="n">gJ</a> <a href="s199.html" class="s">G120-p</a></span></td>
                            <td class="l"><span class="p">Inżopr P04</span>-(n. <span class="p">#ioP</span> <a href="s198.html" class="s">G120-n</a><br><span class="p">Inżopr K04</span>-(p. <span class="p">#iop</span> <a href="s199.html" class="s">G120-p</a></td>
                            <td class="l"><span class="p">WspInfPM W</span>-(N) <span class="p">#WPM</span> <a href="s4.html" class="s">A124-n</a><br><span class="p">Mechatron W</span>-(P) <span class="p">#MTR</span> <a href="s27.html" class="s">G18-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">9</td>
                            <td class="g">14:30-15:15</td>
                            <td class="l"><span class="p">WF (M) Ć-M</span>-(N. <span class="p">W1</span> <a href="s70.html" class="s">Hala2</a></td>
                            <td class="l"><span class="p">PPSystM K04</span>-(n. <span class="p">#Psm</span> <a href="s206.html" class="s">J209-n</a><br><span class="p">PPSystM K04</span>-(p. <span class="p">#PSm</span> <a href="s207.html" class="s">J209-p</a></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">WspInfPM P04</span>-(N) <span class="p">#Wpm</span> <a href="s243.html" class="s">A338-n</a><br><span class="p">PSieciKP W</span>-(P) <span class="p">#PKP</span> <a href="s17.html" class="s">A437-p</a></td>
                            <td class="l"><span class="p">BazDan W</span>-(N) <span class="p">#BDa</span> <a href="s26.html" class="s">G18-n</a><br><span class="p">BazDan W</span>-(P) <span class="p">#bda</span> <a href="s27.html" class="s">G18-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">10</td>
                            <td class="g">15:15-16:00</td>
                            <td class="l"><span class="p">PKM K04</span>-(N. <span class="p">#Pkm</span> <a href="s70.html" class="s">A227-n</a></td>
                            <td class="l"><span class="p">PPSystM K04</span>-(n. <span class="p">#Psm</span> <a href="s206.html" class="s">J209-n</a><br><span class="p">PPSystM K04</span>-(p. <span class="p">#PSm</span> <a href="s207.html" class="s">J209-p</a></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">WspInfPM P04</span>-(N) <span class="p">#Wpm</span> <a href="s243.html" class="s">A338-n</a><br><span class="p">PSieciKP W</span>-(P) <span class="p">#PKP</span> <a href="s17.html" class="s">A437-p</a></td>
                            <td class="l"><span class="p">BazDan W</span>-(N) <span class="p">#BDa</span> <a href="s26.html" class="s">G18-n</a><br><span class="p">BazDan W</span>-(P) <span class="p">#bda</span> <a href="s27.html" class="s">G18-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">11</td>
                            <td class="g">16:15-17:00</td>
                            <td class="l"><span class="p">J niemiecki-(N)</span>-(N. <span class="p">#HE1</span> <a href="s184.html" class="s">A307-n</a><br><span class="p">PrSteroP L04</span>-(P. <span class="p">#psP</span> <a href="s185.html" class="s">G107-p</a></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PPSystM K01-(n.</span> <a href="n111.html" class="n">GF</a> <a href="s204.html" class="s">J207.1-n</a></span><br><span style="font-size:85%"><span class="p">PPSystM K01-(p.</span> <a href="n112.html" class="n">FG</a> <a href="s205.html" class="s">J207.1-p</a></span></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">PPSystM W</span>-(N) <span class="p">#PSM</span> <a href="s26.html" class="s">G18-n</a><br><span class="p">PAplikInt W</span>-(P) <span class="p">#PAI</span> <a href="s27.html" class="s">G18-p</a></td>
                            <td class="l"><span class="p">BazDan W</span>-(N) <span class="p">#BDa</span> <a href="s26.html" class="s">G18-n</a><br><span class="p">BazDan W</span>-(P) <span class="p">#bda</span> <a href="s27.html" class="s">G18-p</a></td>
                           </tr>
                           <tr>
                            <td class="nr">12</td>
                            <td class="g">17:00-17:45</td>
                            <td class="l"><span class="p">PrSteroP L04</span>-(N. <span class="p">#Psp</span> <a href="s184.html" class="s">G107-n</a><br><span class="p">PrSteroP L04</span>-(P. <span class="p">#psP</span> <a href="s185.html" class="s">G107-p</a></td>
                            <td class="l"><span style="font-size:85%"><span class="p">PPSystM K01-(n.</span> <a href="n111.html" class="n">GF</a> <a href="s204.html" class="s">J207.1-n</a></span><br><span style="font-size:85%"><span class="p">PPSystM K01-(p.</span> <a href="n112.html" class="n">FG</a> <a href="s205.html" class="s">J207.1-p</a></span></td>
                            <td class="l">&nbsp;</td>
                            <td class="l"><span class="p">PPSystM W</span>-(N) <span class="p">#PSM</span> <a href="s26.html" class="s">G18-n</a><br><span class="p">PAplikInt W</span>-(P) <span class="p">#PAI</span> <a href="s27.html" class="s">G18-p</a></td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">13</td>
                            <td class="g">18:00-18:45</td>
                            <td class="l"><span style="font-size:85%"><span class="p">SocPsychP Ć-(N)</span> <a href="n169.html" class="n">JJ</a> <a href="s12.html" class="s">A409-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">BazDan K01-(N)</span> <a href="n51.html" class="n">PB</a> <a href="s194.html" class="s">G117-n</a></span><br><span style="font-size:85%"><span class="p">BazDan K01-(P)</span> <a href="n52.html" class="n">BP</a> <a href="s195.html" class="s">G117-p</a></span></td>
                            <td class="l">termin dodatkowy Katedra M7</td>
                            <td class="l">termin dodatkowy Katedra M7</td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">14</td>
                            <td class="g">18:45-19:30</td>
                            <td class="l"><span style="font-size:85%"><span class="p">SocPsychP Ć-(N)</span> <a href="n169.html" class="n">JJ</a> <a href="s12.html" class="s">A409-n</a></span></td>
                            <td class="l"><span style="font-size:85%"><span class="p">J angielski-(n.</span> <a href="n51.html" class="n">#WG4</a> <a href="s194.html" class="s">J204-n</a></span><br><span style="font-size:85%"><span class="p">J angielski-(n.</span> <a href="n52.html" class="n">#WG4</a> <a href="s195.html" class="s">J204-n</a></span></td>
                            <td class="l">termin dodatkowy Katedra M7</td>
                            <td class="l">termin dodatkowy Katedra M7</td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">15</td>
                            <td class="g">19:45-20:30</td>
                            <td class="l"><span class="p">PrSteroP W</span>-(N) <span class="p">#psp</span> <a href="s26.html" class="s">G18-n</a></td>
                            <td class="l"><span style="font-size:85%"><span class="p">BazDan K01-(N)</span> <a href="n51.html" class="n">PB</a> <a href="s194.html" class="s">G117-n</a></span><br><span style="font-size:85%"><span class="p">BazDan K01-(P)</span> <a href="n52.html" class="n">BP</a> <a href="s195.html" class="s">G117-p</a></span></td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                           </tr>
                           <tr>
                            <td class="nr">16</td>
                            <td class="g">20:30-21:15</td>
                            <td class="l"><span class="p">PrSteroP W</span>-(N) <span class="p">#psp</span> <a href="s26.html" class="s">G18-n</a></td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                            <td class="l">&nbsp;</td>
                           </tr>
                          </tbody>
                         </table></td>
                       </tr>
                       <tr>
                        <td align="left"><a href="javascript:window.print()">Drukuj plan</a></td>
                        <td class="op" align="right">
                         <table border="0" cellpadding="0" cellspacing="0">
                          <tbody>
                           <tr>
                            <td align="right">wygenerowano 02.06.2025<br> za pomocą programu <a href="http://www.vulcan.edu.pl/dla_szkol/optivum/plan_lekcji/Strony/wstep.aspx" target="_blank">Plan lekcji Optivum</a><br> firmy <a href="http://www.vulcan.edu.pl/" target="_blank">VULCAN</a></td>
                            <td><img border="0" src="../images/plan_logo.gif" style="margin-left:10" alt="logo programu Plan lekcji Optivum" width="40" height="40"></td>
                           </tr>
                          </tbody>
                         </table></td>
                       </tr>
                       <tr>
                        <td><script type="text/javascript" src="../scripts/powrot.js"></script></td>
                       </tr>
                      </tbody>
                     </table>
                    </div>
                   </body>
                  </html>
            """;

    String listHTML = """
            <html>
                   <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                    <meta http-equiv="Content-Language" content="pl">
                    <meta name="description" content="Wydział Mechaniczny Politechniki Krakowskiej. Lista oddziałów, nauczycieli i sal utworzona za pomoc± programu Plan lekcji Optivum firmy VULCAN">
                    <title>Lista oddziałów, nauczycieli i sal</title>
                    <link rel="stylesheet" href="css/lista.css" type="text/css">
                    <script language="JavaScript1.2" type="text/javascript">
                  if(!document.layers && navigator.userAgent.toLowerCase().indexOf('opera')==-1)
                  document.writeln('<style type="text/css"><!--\\n.nblk{display:none;padding-left:0.5em}\\n.blk{display:block;padding-left:0.5em}\\n-->\\n</style>')
                  </script>
                    <script language="JavaScript1.2" type="text/javascript" src="scripts/plan.js"></script>
                   </head>
                   <body>
                    <div class="logo"><img border="0" src="images/WM_SYGNET.jpg" alt="Logo szkoły">
                    </div>
                    <table border="0" cellpadding="2" cellspacing="0">
                     <tbody>
                      <tr>
                       <td><a href="javascript:spis('oddzialy')"><img name="io" border="0" src="images/minus.gif" width="16" height="16"></a></td>
                       <td><a class="pp" href="javascript:spis('oddzialy')">Oddziały</a></td>
                      </tr>
                      <tr>
                       <td></td>
                       <td>
                        <div class="blk" id="oddzialy">
                         <p class="el"><a href="plany/o1.html" target="plan">11A1</a></p>
                         <p class="el"><a href="plany/o8.html" target="plan">11K2</a></p>
                         <p class="el"><a href="plany/o25.html" target="plan">12K1</a></p>
                         <p class="el"><a href="plany/o26.html" target="plan">12K2</a></p>
                         <p class="el"><a href="plany/o27.html" target="plan">12K3</a></p>
                        </div></td>
                      </tr>
                      <tr>
                       <td><a href="javascript:spis('nauczyciele')"><img name="in" border="0" src="images/plus.gif" width="16" height="16"></a></td>
                       <td><a class="pp" href="javascript:spis('nauczyciele')">Nauczyciele</a></td>
                      </tr>
                      <tr>
                       <td></td>
                       <td>
                        <div class="nblk" id="nauczyciele">
                        </div></td>
                      </tr>
                      <tr>
                       <td><a href="javascript:spis('sale')"><img name="is" border="0" src="images/plus.gif" width="16" height="16"></a></td>
                       <td><a class="pp" href="javascript:spis('sale')">Sale</a></td>
                      </tr>
                      <tr>
                       <td></td>
                       <td>
                        <div class="nblk" id="sale">
                        </div></td>
                      </tr>
                     </tbody>
                    </table>
                   </body>
                  </html>
            """;


}