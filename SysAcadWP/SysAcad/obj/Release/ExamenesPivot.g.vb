﻿#ExternalChecksum("C:\Users\Carlos\Documents\Visual Studio 2012\Projects\SysAcad\SysAcad\ExamenesPivot.xaml","{406ea660-64cf-4c82-b6f0-42d48172a799}","44FB0CAD75647144E2B69441A61E1276")
'------------------------------------------------------------------------------
' <auto-generated>
'     Este código fue generado por una herramienta.
'     Versión de runtime:4.0.30319.34011
'
'     Los cambios en este archivo podrían causar un comportamiento incorrecto y se perderán si
'     se vuelve a generar el código.
' </auto-generated>
'------------------------------------------------------------------------------

Option Strict Off
Option Explicit On

Imports Microsoft.Phone.Controls
Imports System
Imports System.Windows
Imports System.Windows.Automation
Imports System.Windows.Automation.Peers
Imports System.Windows.Automation.Provider
Imports System.Windows.Controls
Imports System.Windows.Controls.Primitives
Imports System.Windows.Data
Imports System.Windows.Documents
Imports System.Windows.Ink
Imports System.Windows.Input
Imports System.Windows.Interop
Imports System.Windows.Markup
Imports System.Windows.Media
Imports System.Windows.Media.Animation
Imports System.Windows.Media.Imaging
Imports System.Windows.Resources
Imports System.Windows.Shapes
Imports System.Windows.Threading



<Microsoft.VisualBasic.CompilerServices.DesignerGenerated()>  _
Partial Public Class ExamenesPivot
    Inherits Microsoft.Phone.Controls.PhoneApplicationPage
    
    Friend WithEvents LayoutRoot As System.Windows.Controls.Grid
    
    Friend WithEvents Pivot As Microsoft.Phone.Controls.Pivot
    
    Friend WithEvents llExamenes As Microsoft.Phone.Controls.LongListSelector
    
    Friend WithEvents promAplazo As System.Windows.Controls.TextBlock
    
    Friend WithEvents promSinAplazo As System.Windows.Controls.TextBlock
    
    Private _contentLoaded As Boolean
    
    '''<summary>
    '''InitializeComponent
    '''</summary>
    <System.Diagnostics.DebuggerNonUserCodeAttribute()>  _
    Public Sub InitializeComponent()
        If _contentLoaded Then
            Return
        End If
        _contentLoaded = true
        System.Windows.Application.LoadComponent(Me, New System.Uri("/SysAcad;component/ExamenesPivot.xaml", System.UriKind.Relative))
        Me.LayoutRoot = CType(Me.FindName("LayoutRoot"),System.Windows.Controls.Grid)
        Me.Pivot = CType(Me.FindName("Pivot"),Microsoft.Phone.Controls.Pivot)
        Me.llExamenes = CType(Me.FindName("llExamenes"),Microsoft.Phone.Controls.LongListSelector)
        Me.promAplazo = CType(Me.FindName("promAplazo"),System.Windows.Controls.TextBlock)
        Me.promSinAplazo = CType(Me.FindName("promSinAplazo"),System.Windows.Controls.TextBlock)
    End Sub
End Class

