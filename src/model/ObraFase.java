package model;

import java.time.LocalDate;

public class ObraFase {
    private Obra obra;
    private Fase fase;
    private LocalDate dtInicioFase;
    private LocalDate dtTerminoFase;

    public ObraFase() { }
    public ObraFase(Obra obra,Fase fase,LocalDate dtInicioFase,LocalDate dtTerminoFase){
        this.obra=obra;this.fase=fase;this.dtInicioFase=dtInicioFase;this.dtTerminoFase=dtTerminoFase;
    }
    public Obra getObra(){return obra;}
    public void setObra(Obra obra){this.obra=obra;}
    public Fase getFase(){return fase;}
    public void setFase(Fase fase){this.fase=fase;}
    public LocalDate getDtInicioFase(){return dtInicioFase;}
    public void setDtInicioFase(LocalDate dtInicioFase){this.dtInicioFase=dtInicioFase;}
    public LocalDate getDtTerminoFase(){return dtTerminoFase;}
    public void setDtTerminoFase(LocalDate dtTerminoFase){this.dtTerminoFase=dtTerminoFase;}
}
