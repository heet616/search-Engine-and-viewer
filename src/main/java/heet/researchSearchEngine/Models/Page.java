package heet.researchSearchEngine.Models;

import java.net.URI;
import java.util.List;

record Page(String name, URI url, List<PageElement> contents) {
}
