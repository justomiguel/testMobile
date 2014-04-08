Partial Public Class Page1
    Inherits PhoneApplicationPage
    Dim aprobado As New List(Of String) From
        {"cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez"}
    Dim desaprobado As New List(Of String) From
        {"uno", "dos", "tres"}




    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
        llExamenes.ItemsSource = App.listaExamenes
        Dim menuItemTodos As New ApplicationBarMenuItem
        menuItemTodos.Text = "Todos"
        Dim menuItemAprobados As New ApplicationBarMenuItem
        menuItemAprobados.Text = "Aprobados"
        Dim menuItemDesaprobados As New ApplicationBarMenuItem
        menuItemDesaprobados.Text = "Desaprobados"
        Dim menuItemAusentes As New ApplicationBarMenuItem
        menuItemAusentes.Text = "Ausentes"
        ApplicationBar.Mode = ApplicationBarMode.Minimized
        ApplicationBar.MenuItems.Add(menuItemTodos)
        ApplicationBar.MenuItems.Add(menuItemAprobados)
        ApplicationBar.MenuItems.Add(menuItemDesaprobados)
        ApplicationBar.MenuItems.Add(menuItemAusentes)
        AddHandler menuItemTodos.Click, AddressOf menuItemTodos_click
        AddHandler menuItemAprobados.Click, AddressOf menuItemAprobados_click
        AddHandler menuItemDesaprobados.Click, AddressOf menuItemDesaprobados_click
        AddHandler menuItemAusentes.Click, AddressOf menuItemAusentes_click
        'Código para animar la navegación
        Dim navIn = New NavigationInTransition
        navIn.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideDownFadeIn}
        navIn.Forward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideUpFadeIn}
        Dim navOut = New NavigationOutTransition
        navOut.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideDownFadeOut}
        navOut.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideUpFadeOut}
        TransitionService.SetNavigationInTransition(Me, navIn)
        TransitionService.SetNavigationOutTransition(Me, navOut)
    End Sub

    Private Sub menuItemTodos_click(ByVal sender As Object, ByVal e As EventArgs)
        For Each examen As App.examen In App.listaExamenes
            examen.Visible = System.Windows.Visibility.Visible
        Next
        llExamenes.ItemsSource = Nothing
        llExamenes.ItemsSource = App.listaExamenes
    End Sub

    Private Sub menuItemAprobados_click(ByVal sender As Object, ByVal e As EventArgs)
        For Each examen As App.examen In App.listaExamenes
            If aprobado.Contains(examen.Nota.Trim.ToLower.ToString) Then
                examen.Visible = System.Windows.Visibility.Visible
            Else
                examen.Visible = System.Windows.Visibility.Collapsed
            End If
        Next
        llExamenes.ItemsSource = Nothing
        llExamenes.ItemsSource = App.listaExamenes
    End Sub

    Private Sub menuItemDesaprobados_click(ByVal sender As Object, ByVal e As EventArgs)
        For Each examen As App.examen In App.listaExamenes
            If desaprobado.Contains(examen.Nota.Trim.ToLower.ToString) Then
                examen.Visible = System.Windows.Visibility.Visible
            Else
                examen.Visible = System.Windows.Visibility.Collapsed
            End If
        Next
        llExamenes.ItemsSource = Nothing
        llExamenes.ItemsSource = App.listaExamenes
    End Sub

    Private Sub menuItemAusentes_click(ByVal sender As Object, ByVal e As EventArgs)
        For Each examen As App.examen In App.listaExamenes
            If examen.Nota.Trim.ToLower.ToString = "ausen." Then
                examen.Visible = System.Windows.Visibility.Visible
            Else
                examen.Visible = System.Windows.Visibility.Collapsed
            End If
        Next
        llExamenes.ItemsSource = Nothing
        llExamenes.ItemsSource = App.listaExamenes
    End Sub
End Class
