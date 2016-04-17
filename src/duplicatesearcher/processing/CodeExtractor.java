package duplicatesearcher.processing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * This class finds and removes codes from a GitHub issue and its comments that
 * is formatted with GitHub markdown (any sequence of text starting with one or
 * multiple "`" and end with the same amount of "`").
 */
public class CodeExtractor
{
	public final static String CODE = "(`+[^`]+`+)";
	private final static Pattern CODE_PATTERN = Pattern.compile(CODE);

	private final Issue issue;
	private final List<Comment> comments;

	public CodeExtractor(Issue issue, List<Comment> comments)
	{
		this.issue = issue;
		this.comments = comments;
	}

	private Set<String> findCode(CharSequence input)
	{
		Set<String> code = new HashSet<String>();
		Matcher match = CODE_PATTERN.matcher(input);
		while(match.find())
		{
			final int start = match.start();
			final int end = match.end();
			String lineOfCode = input.subSequence(start, end).toString();
			lineOfCode = lineOfCode.replaceAll("`", "");
			code.add(lineOfCode);
		}

		return code;
	}

	public Set<String> extractCode()
	{
		Set<String> codeIssue = findCode(issue.getBody());
		removeCodeFromIssue(issue);

		Set<String> codeComments = getCodeFromComments(comments);
		removeCodeFromComments(comments);

		codeIssue.addAll(codeComments);
		return codeIssue;
	}

	private void removeCodeFromIssue(Issue issue)
	{
		final String newBody = issue.getBody().replaceAll(CodeExtractor.CODE, "");
		issue.setBody(newBody);
	}

	private Set<String> getCodeFromComments(List<Comment> comments)
	{
		Set<String> code = new HashSet<String>();

		for(Comment comment : comments)
		{
			Set<String> codeFromComment = findCode(comment.getBody());
			code.addAll(codeFromComment);
		}

		return code;
	}

	private void removeCodeFromComments(List<Comment> comments)
	{
		for(Comment comment : comments)
		{
			String newBody = comment.getBody().replaceAll(CodeExtractor.CODE, "");
			comment.setBody(newBody);
		}
	}

}
