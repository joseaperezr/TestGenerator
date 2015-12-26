package es.infinitysoft.generator.test.service;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import es.infinitysoft.generator.test.model.Document;
import es.infinitysoft.generator.test.model.Field;
import es.infinitysoft.generator.test.model.Method;
import es.infinitysoft.generator.test.model.Service;

@ManagedBean(name = "methodService")
@ApplicationScoped
public class MethodService {

	public TreeNode createDocuments(Service service) {

		TreeNode root = new DefaultTreeNode(new Document("WebService", "-",
				service.name), null);

		for (Method method : service.methods) {
			TreeNode nodeMethod = new DefaultTreeNode(new Document(method.name,
					"-", "Método"), root);
			// Request's.
			if (method.response != null) {
				TreeNode request = new DefaultTreeNode(new Document(
						method.request.getName(), "-", "Request"), nodeMethod);
				for (Field field : method.request.getElement().fields) {
					createNodeField(field,request);
					/*TreeNode nodeField = new DefaultTreeNode(new Document(
							field.name, "-", field.type), request);*/
				}
			}
			// Response's.
			if (method.response != null) {
				TreeNode response = new DefaultTreeNode(new Document(
						method.response.getName(), "-", "Response"), nodeMethod);
				for (Field field : method.response.getElement().fields) {
					createNodeField(field,response);
					/*TreeNode nodeField = new DefaultTreeNode(new Document(
							field.name, "-", field.type), response);*/
				}
			}
		}

		return root;
	}
	
	
	private void createNodeField(Field field, TreeNode parent){
		if (field.isComplexType()){
   		   TreeNode nodeFieldC = new DefaultTreeNode(new Document(
				field.name, "-", field.type), parent);
   		   for (Field fieldC : field.getFields()){
   			createNodeField(fieldC ,nodeFieldC);   
   		   }
		}else {
			TreeNode nodeFieldC = new DefaultTreeNode(new Document(
				field.name, "-", field.type), parent);
		}		
	}
	
	

	public TreeNode createCheckboxDocuments() {
		TreeNode root = new CheckboxTreeNode(new Document("Files", "-",
				"Folder"), null);

		TreeNode documents = new CheckboxTreeNode(new Document("Documents",
				"-", "Folder"), root);
		TreeNode pictures = new CheckboxTreeNode(new Document("Pictures", "-",
				"Folder"), root);
		TreeNode movies = new CheckboxTreeNode(new Document("Movies", "-",
				"Folder"), root);

		TreeNode work = new CheckboxTreeNode(
				new Document("Work", "-", "Folder"), documents);
		TreeNode primefaces = new CheckboxTreeNode(new Document("PrimeFaces",
				"-", "Folder"), documents);

		// Documents
		TreeNode expenses = new CheckboxTreeNode("document", new Document(
				"Expenses.doc", "30 KB", "Word Document"), work);
		TreeNode resume = new CheckboxTreeNode("document", new Document(
				"Resume.doc", "10 KB", "Word Document"), work);
		TreeNode refdoc = new CheckboxTreeNode("document", new Document(
				"RefDoc.pages", "40 KB", "Pages Document"), primefaces);

		// Pictures
		TreeNode barca = new CheckboxTreeNode("picture", new Document(
				"barcelona.jpg", "30 KB", "JPEG Image"), pictures);
		TreeNode primelogo = new CheckboxTreeNode("picture", new Document(
				"logo.jpg", "45 KB", "JPEG Image"), pictures);
		TreeNode optimus = new CheckboxTreeNode("picture", new Document(
				"optimusprime.png", "96 KB", "PNG Image"), pictures);

		// Movies
		TreeNode pacino = new CheckboxTreeNode(new Document("Al Pacino", "-",
				"Folder"), movies);
		TreeNode deniro = new CheckboxTreeNode(new Document("Robert De Niro",
				"-", "Folder"), movies);

		TreeNode scarface = new CheckboxTreeNode("mp3", new Document(
				"Scarface", "15 GB", "Movie File"), pacino);
		TreeNode carlitosWay = new CheckboxTreeNode("mp3", new Document(
				"Carlitos' Way", "24 GB", "Movie File"), pacino);

		TreeNode goodfellas = new CheckboxTreeNode("mp3", new Document(
				"Goodfellas", "23 GB", "Movie File"), deniro);
		TreeNode untouchables = new CheckboxTreeNode("mp3", new Document(
				"Untouchables", "17 GB", "Movie File"), deniro);

		return root;
	}
}
