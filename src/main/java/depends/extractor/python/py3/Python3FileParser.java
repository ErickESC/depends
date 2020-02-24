package depends.extractor.python.py3;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import depends.entity.Entity;
import depends.entity.FileEntity;
import depends.entity.repo.EntityRepo;
import depends.extractor.FileParser;
import depends.extractor.python.Python3Lexer;
import depends.extractor.python.Python3Parser;
import depends.extractor.ruby.IncludedFileLocator;
import depends.relations.Inferer;

public class Python3FileParser implements FileParser {

	private String fileFullPath;
	private EntityRepo entityRepo;
	private Inferer inferer;
	private IncludedFileLocator includeFileLocator;
	private Python3Processor processor;

	public Python3FileParser(String fileFullPath, EntityRepo entityRepo, IncludedFileLocator includeFileLocator,
			Inferer inferer, Python3Processor pythonProcessor) {
		this.fileFullPath = fileFullPath;
		this.entityRepo = entityRepo;
		this.inferer = inferer;
		this.includeFileLocator = includeFileLocator;
		this.processor = pythonProcessor;
	}

	@Override
	public void parse() throws IOException {
		/** If file already exist, skip it */
		Entity fileEntity = entityRepo.getEntity(fileFullPath);
		if (fileEntity!=null && fileEntity instanceof FileEntity) {
			return;
		}
        CharStream input = CharStreams.fromFileName(fileFullPath);
        Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        
        Python3Parser parser = new Python3Parser(tokens);
        Python3CodeListener bridge = new Python3CodeListener(fileFullPath, entityRepo,inferer, includeFileLocator, processor);
	    ParseTreeWalker walker = new ParseTreeWalker();
	    walker.walk(bridge, parser.file_input());
	    
		fileEntity = entityRepo.getEntity(fileFullPath);
		fileEntity.inferEntities(inferer);
		((FileEntity)fileEntity).cacheAllExpressions();
	}

}
