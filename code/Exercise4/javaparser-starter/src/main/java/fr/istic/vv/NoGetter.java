package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class NoGetter extends VoidVisitorWithDefaults<Void> {

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        if (!declaration.isPublic()) return;

        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));

        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) member;

                // Check if the class has a public getter for this field
                if (!hasPublicGetter(declaration, fieldDeclaration)) {
                    System.out.println("  Attribute without public getter: " +
                            fieldDeclaration.getVariables().get(0).getName());
                }
            }
        }
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        if(!declaration.isPublic()) return;
        System.out.println("  " + declaration.getDeclarationAsString(true, true));
    }

    // Check if the class has a public getter for a specific field
    private boolean hasPublicGetter(ClassOrInterfaceDeclaration declaration, FieldDeclaration fieldDeclaration) {
        String fieldName = fieldDeclaration.getVariables().get(0).getNameAsString();
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;

                // Check if the method is public and follows the naming convention of a getter
                if (method.isPublic() && method.getNameAsString().equals(getterMethodName)
                        && method.getType().equals(fieldDeclaration.getElementType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
