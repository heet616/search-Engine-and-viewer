📄 Research Paper Search Engine

A powerful desktop-based **Research Paper Search Engine** that allows you to search, filter, and view research papers from multiple websites in one place using their public APIs.  
It is designed to make academic research easier, faster, and more organized.

🚀 Features

- **Multi-Source Search**: Search research papers across multiple platforms (e.g., arXiv, OenAlex, etc.) using their APIs.
- **Advanced Filtering** *(Coming Soon)*: Filter results by keywords, authors, publication year, source, and more.
- **Tab-Based Searching**: Perform and manage multiple search queries at the same time using a tabbed interface.
- **Detailed View**: View full details of any selected paper such as title, abstract, authors, publication date, and source link.
- **PDF Viewing & Download** *(Coming Soon)*: Download the research paper's PDF and view it directly within the application.
- **Clean UI**: A modern, user-friendly JavaFX-based interface.

🖥️ Technologies Used

- **Java**
- **JavaFX** (UI)
- **HTTP APIs** (For fetching papers)
- **XML / JSON Parsers** (For handling API responses)
- **PDF Viewer Integration** (Coming Soon)

🌐 Supported APIs

The search engine fetches research papers from multiple sources including:

- **arXiv API**
- **OpenAlex API**
- **CrossRef API**
- **Semantic Scholar API**

*(More sources to be added)*


📂 How to Use

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/research-paper-search-engine.git
   ```

2. **Set API Keys (if required)**
   - Some APIs may require API keys. Add your keys in the `config.properties` or relevant config file.

3. **Run the Application**
   ```bash
   cd research-paper-search-engine
   ./gradlew run
   ```

4. **Start Searching**
   - Enter your search keyword in the search bar.
   - Open multiple tabs to search for different topics.
   - Filter the results (Feature coming soon).
   - Select a paper to view detailed information.
   - Download and view PDFs inside the app (Feature coming soon).
