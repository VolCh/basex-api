﻿/*
 * -----------------------------------------------------------------------------
 *
 * This example shows how BaseX commands can be performed via the C# API.
 * The execution time will be printed along with the result of the command.
 *
 * -----------------------------------------------------------------------------
 * (C) Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * -----------------------------------------------------------------------------
 */
using System;
using System.Diagnostics;
using System.IO;

namespace BaseXClient
{
  public class Example
  {
    public static void Main(string[] args)
    {
      // initialize timer
      Stopwatch watch = new Stopwatch();
      watch.Start();
      
      // command to be performed
      string cmd = "xquery 1 to 10";
      
      try
      {
        // create session
        Session session = new Session("localhost", 1984, "admin", "admin");

        // Version 1: perform command and show result or error output
        if (session.Execute(cmd))
        {
        	Console.WriteLine(session.Result);
        }
        else
        {
          Console.WriteLine(session.Info);
        }
        
        // Version 2 (faster): send result to the specified output stream
        Stream stream = Console.OpenStandardOutput();
        if (!session.Execute(cmd, stream))
        {
          Console.WriteLine(session.Info);
        }

        // close session
        session.Close();
        
        // print time needed
        Console.WriteLine("\n" + watch.ElapsedMilliseconds + " ms.");
      }
      catch (IOException e)
      {
        // print exception
        Console.WriteLine(e.Message);
      }
    }
  }
}