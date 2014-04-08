Imports System.Windows.Media.Imaging
Partial Public Class Horario
    Inherits PhoneApplicationPage
    Dim initialScale As Double = 0

    Public Sub New()
        InitializeComponent()
        Dim bm As BitmapImage = New BitmapImage(App.uriCursado)
        imHorario.Source = bm
        SupportedOrientations = SupportedPageOrientation.Portrait
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
    Private Sub onPinchStarted(ByVal sender As Object, ByVal e As PinchStartedGestureEventArgs)
        initialScale = transform.ScaleX
    End Sub
    Private Sub onPinchDelta(ByVal sender As Object, ByVal e As PinchGestureEventArgs)
        transform.ScaleX = initialScale * e.DistanceRatio
        transform.ScaleY = initialScale * e.DistanceRatio
    End Sub

    Private Sub GestureListener_DragDelta(sender As Object, e As DragDeltaGestureEventArgs)
        transform.TranslateX += e.HorizontalChange
        transform.TranslateY += e.VerticalChange
        e.Handled = True
    End Sub
End Class
