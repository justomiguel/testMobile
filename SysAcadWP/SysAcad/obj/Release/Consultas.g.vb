﻿#ExternalChecksum("C:\Users\Carlos\Documents\Visual Studio 2012\Projects\SysAcad\SysAcad\Consultas.xaml","{406ea660-64cf-4c82-b6f0-42d48172a799}","7EB5E48F1CF7FEFBA4BB48F60F53F072")
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
Partial Public Class Page3
    Inherits Microsoft.Phone.Controls.PhoneApplicationPage
    
    Friend WithEvents LayoutRoot As System.Windows.Controls.Grid
    
    Friend WithEvents ContentPanel As System.Windows.Controls.StackPanel
    
    Friend WithEvents lpPara As Microsoft.Phone.Controls.ListPicker
    
    Friend WithEvents lpAsunto As Microsoft.Phone.Controls.ListPicker
    
    Friend WithEvents tbMail As System.Windows.Controls.TextBox
    
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
        System.Windows.Application.LoadComponent(Me, New System.Uri("/SysAcad;component/Consultas.xaml", System.UriKind.Relative))
        Me.LayoutRoot = CType(Me.FindName("LayoutRoot"),System.Windows.Controls.Grid)
        Me.ContentPanel = CType(Me.FindName("ContentPanel"),System.Windows.Controls.StackPanel)
        Me.lpPara = CType(Me.FindName("lpPara"),Microsoft.Phone.Controls.ListPicker)
        Me.lpAsunto = CType(Me.FindName("lpAsunto"),Microsoft.Phone.Controls.ListPicker)
        Me.tbMail = CType(Me.FindName("tbMail"),System.Windows.Controls.TextBox)
    End Sub
End Class

