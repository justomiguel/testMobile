﻿#ExternalChecksum("C:\Users\Carlos\Documents\Visual Studio 2012\Projects\SysAcad\SysAcad\MainPage.xaml","{406ea660-64cf-4c82-b6f0-42d48172a799}","136CD1C1AAAB179DC430EA4017374535")
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
Partial Public Class MainPage
    Inherits Microsoft.Phone.Controls.PhoneApplicationPage
    
    Friend WithEvents LayoutRoot As System.Windows.Controls.Grid
    
    Friend WithEvents TitlePanel As System.Windows.Controls.StackPanel
    
    Friend WithEvents ContentPanel As System.Windows.Controls.Grid
    
    Friend WithEvents tbLegajoLogin As System.Windows.Controls.TextBox
    
    Friend WithEvents tbPassLogin As System.Windows.Controls.PasswordBox
    
    Friend WithEvents btLogin As System.Windows.Controls.Button
    
    Friend WithEvents lbPassHint As System.Windows.Controls.TextBlock
    
    Friend WithEvents btUseOffline As System.Windows.Controls.Button
    
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
        System.Windows.Application.LoadComponent(Me, New System.Uri("/SysAcad;component/MainPage.xaml", System.UriKind.Relative))
        Me.LayoutRoot = CType(Me.FindName("LayoutRoot"),System.Windows.Controls.Grid)
        Me.TitlePanel = CType(Me.FindName("TitlePanel"),System.Windows.Controls.StackPanel)
        Me.ContentPanel = CType(Me.FindName("ContentPanel"),System.Windows.Controls.Grid)
        Me.tbLegajoLogin = CType(Me.FindName("tbLegajoLogin"),System.Windows.Controls.TextBox)
        Me.tbPassLogin = CType(Me.FindName("tbPassLogin"),System.Windows.Controls.PasswordBox)
        Me.btLogin = CType(Me.FindName("btLogin"),System.Windows.Controls.Button)
        Me.lbPassHint = CType(Me.FindName("lbPassHint"),System.Windows.Controls.TextBlock)
        Me.btUseOffline = CType(Me.FindName("btUseOffline"),System.Windows.Controls.Button)
    End Sub
End Class

