package com.edatasource.receipts.parser.groupon;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Efficient DOM traversal
 * 
 * @author ciri-cuervo
 *
 */
public class DomTraversalUtil {

	private Element element;
	private int nthChild;

	private DomTraversalUtil(Element element, int nthChild) {
		this.element = element;
		this.nthChild = nthChild;
	}

	public static Element first(String tag, Node node)
	{
		return nthChildNthDescendant(tag, 1, 1, node);
	}

	public static Element first(String tag, List<? extends Node> nodes)
	{
		for (Node node : nodes)
		{
			Element element = first(tag, node);
			if (element != null)
			{
				return element;
			}
		}
		return null;
	}

	public static Element nthDescendant(String tag, int d, Node node)
	{
		return nthChildNthDescendant(tag, 1, d, node);
	}

	public static Element nthChildFirstDescendant(String tag, int n, Node node)
	{
		return nthChildNthDescendant(tag, n, 1, node);
	}

	private static Element nthChildNthDescendant(String tag, int n, int d, Node node)
	{
		if (n < 1 || d < 1)
		{
			return null;
		}
		return nthChildNthDescendant(tag, n, d, 1, 1, node, node).element;
	}

	private static DomTraversalUtil nthChildNthDescendant(String tag, int n, int d, int count, int depth,
			Node root, Node node)
	{
		if (node != root && node.nodeName().equals(tag))
		{
			if (count == n)
			{
				if (depth == d)
				{
					return new DomTraversalUtil((Element) node, count);
				}
				return nthChildNthDescendant(tag, n, d, 1, depth + 1, node, node);
			}
			return new DomTraversalUtil(null, count + 1);
		}

		for (Node child : node.childNodes())
		{
			DomTraversalUtil found = nthChildNthDescendant(tag, n, d, count, depth, root, child);
			if (found.element != null)
			{
				return found;
			}
			count = found.nthChild;
		}

		return new DomTraversalUtil(null, count);
	}

}
