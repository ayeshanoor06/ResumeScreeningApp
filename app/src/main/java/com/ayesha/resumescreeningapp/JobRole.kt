package com.ayesha.resumescreeningapp

data class JobRole(
    val name: String,
    val keywords: List<String>
)

object JobRoles {

    val roles = listOf(

        JobRole(
            name = "Frontend Development",
            keywords = listOf(
                "HTML",
                "CSS",
                "JavaScript",
                "React",
                "Next.js",
                "Bootstrap",
                "Responsive",
                "Git"
            )
        ),

        JobRole(
            name = "App Development",
            keywords = listOf(
                "Kotlin",
                "Android",
                "Java",
                "Flutter",
                "Dart",
                "React Native",
                "Firebase",
                "Mobile",
                "Git"
            )
        ),

        JobRole(
            name = "Backend Development",
            keywords = listOf(
                "Python",
                "Django",
                "Node.js",
                "Express",
                "API",
                "SQL",
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "REST",
                "Git"
            )
        ),

        JobRole(
            name = "Chatbot Development",
            keywords = listOf(
                "Python",
                "NLP",
                "Machine Learning",
                "TensorFlow",
                "PyTorch",
                "Chatbot",
                "API",
                "JSON",
                "Intent",
                "Git"
            )
        ),

        JobRole(
            name = "Graphic Design",
            keywords = listOf(
                "Photoshop",
                "Illustrator",
                "Figma",
                "UI",
                "UX",
                "Branding",
                "Typography",
                "Canva"
            )
        ),

        JobRole(
            name = "Data Analytics",
            keywords = listOf(
                "Python",
                "Pandas",
                "NumPy",
                "SQL",
                "Excel",
                "Data Analysis",
                "Matplotlib",
                "Statistics",
                "Data Visualization"
            )
        )
    )
}