
Partial Public Class Page5
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        SupportedOrientations = SupportedPageOrientation.Portrait
    End Sub

    Dim initialScale As Double = 0
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
