package com.example.demo.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Genero;
import com.example.demo.model.Libro;
import com.example.demo.service.GeneroService;
import com.example.demo.service.LibroService;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
@RequestMapping("/libreria")
public class LibroController {
	
	@Autowired
	private LibroService libroService;
	
	@Autowired
	private GeneroService generoService;
	
	
	/***********LISTADO*************/
	@GetMapping("/libros")
	public String getAllProduct(Model model) {
		
		List<Libro> lislibros = libroService.getAllLibros();
		model.addAttribute("libros", lislibros);
		
		return "librosList";
	}
	
	/***********REGISTRO*************/
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("generos", generoService.getAllGeneros());
		return "librosRegister";
	}
	
	@PostMapping("/register")	
	public String createlibro(@RequestParam("nameLibro") String nameLibro,
							  @RequestParam("nameAutor") String nameAutor,
							  @RequestParam("fecPublicacion") Date fecPublicacion,
							  @RequestParam("id") Long id,
							  Model model) {
		
		Libro libro = new Libro();
		libro.nombreLibro = nameLibro;
		libro.nombreAutor = nameAutor;
		libro.fecPublicacion = fecPublicacion;
				
		Genero genero = generoService.getGeneroById(id);
		libro.genero = genero;
		
		libroService.createLibro(libro);
		model.addAttribute("libros", libroService.getAllLibros());
		
		return "librosList";
		
		
		
	}
			
	/***********EDIT*************/
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		Libro libro = libroService.getLibroById(id);
		
		model.addAttribute("libro", libro);
		model.addAttribute("generos", generoService.getAllGeneros());
		return "librosEdit";
	}
	
	@PostMapping("/edit")	
	public String createProduct(@RequestParam("id") Long id,
								@RequestParam("nameLibro") String nameLibro,
			  					@RequestParam("nameAutor") String nameAutor,
	  							@RequestParam("fecPublicacion") Date fecPublicacion,
	  							@RequestParam("idGenero") Long idGenero,
	  							Model model) {
		
		Libro libro = libroService.getLibroById(id);
		libro.nombreLibro = nameLibro;
		libro.nombreAutor = nameAutor;
		libro.fecPublicacion = fecPublicacion;
		
		Genero genero = generoService.getGeneroById(idGenero);
		
		libro.genero = genero;
		
		libroService.createLibro(libro);
		
		model.addAttribute("libros", libroService.getAllLibros());
		model.addAttribute("generos", generoService.getAllGeneros());
		
		return "librosList";
		
		
	}
			
	/***********DELETE*************/
	@GetMapping("delete/{id}")
	public String deleteLibro(@PathVariable Long id, Model model) {
		libroService.deleteLibro(id);
		
		model.addAttribute("libros", libroService.getAllLibros());
		model.addAttribute("generos", generoService.getAllGeneros());
		
		return "librosList";
	}
	
	/***********REPORTE*************/	
	@GetMapping("/reporte")
	public void report(HttpServletResponse response) throws JRException, IOException {
		//1. Acceder reporte
		InputStream jasperStream = this.getClass().getResourceAsStream("/reportes/libreriaReporte.jasper");
		
		//2. Preparar los datos
		Map<String, Object> params = new HashMap<>();
		params.put("usuario", "Municipalidad de Lima");
		
		List<Libro> listLibros = libroService.getAllLibros();
		
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(listLibros);
		
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
		
		//3. Configuracion
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "filename=libros.pdf");
		
		//4. Exportar reporte
		final OutputStream outputStream = response.getOutputStream();		
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}
	

}
