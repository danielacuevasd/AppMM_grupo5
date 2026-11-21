package com.example.mscatalogo.config;

import com.example.mscatalogo.model.Contenido;
import com.example.mscatalogo.repository.ContenidoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final ContenidoRepository repositorio;

    public DatosIniciales(ContenidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) return;
        repositorio.saveAll(List.of(
                new Contenido("Receta", "Sablés de cacao", "Galletas",
                        "Galletas de mantequilla y cacao con textura arenosa, ideales para vitrina.", 60, "Básico"),
                new Contenido("Receta", "Galletas de avena y miel", "Galletas",
                        "Receta simple de galletas suaves con avena y un toque de miel.", 40, "Básico"),
                new Contenido("Curso", "Templado de chocolate", "Chocolatería",
                        "Aprende a controlar la temperatura para lograr brillo y quiebre perfecto.", 40, "Intermedio"),
                new Contenido("Receta", "Mousse de chocolate", "Chocolatería",
                        "Postre aireado de chocolate semiamargo, listo para porciones individuales.", 45, "Básico"),
                new Contenido("Receta", "Croissant de mantequilla", "Masas de hoja",
                        "Laminado paso a paso para obtener capas finas y un horneado dorado.", 180, "Avanzado"),
                new Contenido("Video", "Plegado de hojaldre", "Masas de hoja",
                        "Técnica de plegado simple y doble para capas parejas.", 15, "Intermedio"),
                new Contenido("Curso", "Pastelería de vitrina", "Pastelería",
                        "Cursos de bases, rellenos y montaje para una vitrina atractiva.", 90, "Intermedio"),
                new Contenido("Receta", "Torta tres leches", "Pastelería",
                        "Bizcocho húmedo con tres leches y una cobertura ligera.", 75, "Básico"),
                new Contenido("Video", "Crema pastelera sin grumos", "Pastelería",
                        "Consejos para cocinar y enfriar la crema sin que se corte.", 12, "Básico"),
                new Contenido("Receta", "Helado de vainilla", "Heladería",
                        "Base cremosa de vainilla para servir en copa o cono.", 50, "Básico"),
                new Contenido("Curso", "Pan de masa madre", "Horneo",
                        "Mantención del cultivo, fermentación y horneo en casa o en local.", 120, "Avanzado"),
                new Contenido("Receta", "Empanadas de pino al horno", "Empanadas",
                        "Masa resistente y relleno jugoso, con consejos de cocción uniforme.", 90, "Intermedio"),
                new Contenido("Receta", "Brioche", "Bollería",
                        "Masa enriquecida con mantequilla y huevo para pan dulce suave.", 150, "Intermedio"),
                new Contenido("Video", "Moldeado de bollería", "Bollería",
                        "Formas básicas para presentar pan dulce de manera uniforme.", 10, "Básico")
        ));
    }
}
