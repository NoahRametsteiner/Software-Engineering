import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class JCodex extends JFrame implements Runnable, WindowListener, MouseMotionListener, KeyListener{

	boolean ThreadIsRunning = true;		//Stop Thread If It Is False
	
	public double	pi = 3.14159,		//PI
					p2 = pi/2,			//
					p3 = 3*pi/2,		//
					dr = 0.0174533;		//One Degree In Radians
					
	public double	px = 350,			//Player X
					py = 350,			//Player Y
					pdx = 0, 			//Player Delta X
					pdy = 0, 			//Player Delta Y
					pa = 0,  			//Player Angle
					ud = 0,  			//Player Look Up/Down Offset
					shade=1;			//Shading Of Texture
	
	public Boolean 	xpc = false,		//X Positive Collision
					xnc = false,		//X Negativ Collision
					ypc = false,		//Y Positive Collision
					ync = false,		//Y Negativ Collision
					goal = false,		//If You Get The Goal
					lev = true;			//If Tha Map Should Update


	
	int map[] = {};						//Map Array
	int texture[] = {};					//Texture Array 
	
	//Dyn Map Settings
	public int		dynbitshift = 8,	//Shift The Bit 6 Bits
					dyndof = 16,		//Var For Changing DrawRay
					dynmapsize = 16,	//Var For Changing DrawRay 
					dynmaps = 256,		//Var For Changing DrawRay
					mapx = 16,			//Lengh If Tail
					mapy = 16,			//Width If Tail
					maps = 256,			//Nummber Of Tails
					wx = 16*16+6,		//Windows Size X
					wy = 16*16+6,		//Windows Size Y
					fx = 1440,			//Frame Size X
					fy = 900,			//Frame Size Y
					lvl = 0,			//What Level The Player Is On
					ps = 10;			//Player Speed
	JCodex(){
		pdx=Math.cos(pa)*5; //First Call
		pdy=Math.sin(pa)*5; //First Call
		
		this.setSize(fx,fy);
		this.setVisible(true);
		this.setResizable(false);
		this.addWindowListener(this); 
		this.addKeyListener(this);
		this.setBackground(Color.gray);
		
		Thread t = new Thread(this);
		t.start();
		
	}
	
	//Thread
	public void run(){
		Texture brick = new Texture();
		
		Save code = new Save(); 		//Check If File Exists Or Else Creates File    
		px = code.getplayerx(); 		//Gets Player X Position From File
		py = code.getplayery();			//Gets Player Y Position From File
		
		repaint();
		while(ThreadIsRunning){ 
			lvl = code.getlvl();		//Gets Level From File
			
			Graphics graf=getGraphics();
			
			Map level = new Map();
			level.setLevel(lvl);		//Sets The Level Number in Map.java
			map = level.getMap();		//Gets The Map Corresponding To The Level Number
			
			drawRay(graf,map,brick);	//Draws Ray And World
			
			nextlvl(code);				//Checks If The Player Walks In a Portal
		
		}try{
			Thread.sleep(1);
		} catch (InterruptedException ignore) {
			ThreadIsRunning = false;
		}
	}

	public void nextlvl(Save code) {
		
        int xo2=0;
        if(pdx<0) {
        	xo2=-100;
        }else {
        	xo2=100;
        }
        int yo2=0;
        if(pdy<0) {
        	yo2=-100;
        }else {
        	yo2=100;
        }
        int ipxplusxo=(int) ((px+xo2)/dynmaps);
        int ipyplusyo=(int) ((py+yo2)/dynmaps);

        if(map[ipyplusyo*mapx+ipxplusxo]==2) {
        	
        	if(lvl==2) {					//If The Level Is 2 And The Player Is At A Portal It Resets The Level To The First One
        		lvl=0;	
        		px = 350;
				py = 350;	
				code.setWrite(lvl,px,py);	//Writes Level, Player X, Player Y In File
        	}else {
        		lvl++;						//Sets The Level Number To The Next Level
        		code.setWrite(lvl,px,py);	//Writes Level, Player X, Player Y In File
        	}
        }
	}
	
	
	public void drawRay(Graphics graf, int map[], Texture brick) {
		
		int vmt=0,hmt=0;
		
		int 	r=0, 			//Amount Of Rays
				mx=0, 			//Map X
				my=0, 			//Map Y
				mp=0, 			//Map Position
				dof=0; 			//Depth Of Field
		double 	rx=0, 			//Ray X
				ry=0, 			//Ray Y
				xo=0,			//X Offset
				yo=0,			//Y Offset
				ra=0, 			//Rays Angle
				dist=0;			//Distanze
	
		ra=pa-dr*34; //Player Angle In Ray Angle (Rad) With 30° Offset
		if(ra<0) {
			ra+=2*pi;
		}
		if(ra>2*pi) {
			ra-=2*pi;
		}
		
		for(r=1;r<68;r++) {
			
			//Check Horizontal Lines
			dof=0;
			double 	dish=100000000,			//Distance
					hx=px,					//Player X In Horizontal X
					hy=py,					//Player Y In Horizontal Y
					aTan = -1/Math.tan(ra);	//Negativ Tan

			//Looking Up
			if(ra>pi) {
				ry=(((int)py>>dynbitshift)<<dynbitshift)-0.0001;
				rx=(py-ry)*aTan+px;
				yo=-dynmaps;
				xo=-yo*aTan;
			}
			//Looking Down
			if(ra<pi) {
				ry=((((int)py>>dynbitshift)<<dynbitshift)+maps);
				rx=(py-ry)*aTan+px;

				yo=dynmaps;
				xo=-yo*aTan;
			}
			//Looking Straight Left Or Right
			if(ra==0 || ra==pi) {
				rx=px; 
				ry=py;
				dof=dyndof;	
			}
			while(dof<dynmapsize) {
				mx=(int)(rx)>>dynbitshift;
				if(mx<0) {
					mx=0;
				}
				my=(int)(ry)>>dynbitshift;
				mp=my*mapx+mx;
				
				if(mp>0 && mp<mapx*mapy && map[mp]>0) {
					vmt=map[mp]-1;
					hx=rx;
					hy=ry;
					dish=(Math.sqrt((hx-px)*(hx-px) + (hy-py)*(hy-py)));
					
					dof=dyndof;
				}else {
					rx+=xo;
					ry+=yo;
					dof+=1;
				}
			}
			
			//Check Vertical Lines
			dof=0;
			double 	disv=100000000,			//Distance
					vx=px,					//Player X In Horizontal X
					vy=py,					//Player Y In Horizontal Y
					nTan = -Math.tan(ra);	//Negativ Tan

			//Looking Up
			if(ra>p2 && ra<p3) {
				rx=(((int)px>>dynbitshift)<<dynbitshift)-0.0001;
				ry=(px-rx)*nTan+py;
				xo=-dynmaps;
				yo=-xo*nTan;
			}
			//Looking Down
			if(ra<p2 || ra>p3) {
				rx=((((int)px>>dynbitshift)<<dynbitshift)+maps);
				ry=(px-rx)*nTan+py;

				xo=dynmaps;
				yo=-xo*nTan;
			}
			//Looking Straight Left Or Right
			if(ra==0 || ra==pi) {
				rx=px; 
				ry=py;
				dof=dyndof;	
			}
			while(dof<dyndof) {
				mx=(int)(rx)>>dynbitshift;
				my=(int)(ry)>>dynbitshift;
				if(my<0) {
					my=0;
				}
				mp=my*mapx+mx;
				
				if(mp>0 && mp<mapx*mapy && map[mp]>0) {
					vx=rx;
					vy=ry;
					disv=(Math.sqrt((vx-px)*(vx-px) + (vy-py)*(vy-py)));
					
					dof=dyndof;
				}else {
					rx+=xo;
					ry+=yo;
					dof+=1;
				}
			}
			
			Graphics draw3d = getGraphics();	
			Graphics test = getGraphics();	
			

			
			if(disv<dish) {
				rx=vx;
				ry=vy;
				dist=disv;
				texture = brick.getTexture1();
				shade=0.5;
			}
			if(dish<disv) {
				rx=hx;
				ry=hy;
				dist=dish;
				texture = brick.getTexture1();
				shade=1;
			}			
			
			draw3d(disv, dish, draw3d, test,dist,r,pa,ra,rx,ry,mp,brick);
			
			ra+=dr;
			if(ra<0) {ra+=2*pi;}
			if(ra>2*pi) {ra-=2*pi;}
		}
	}
	
	public void draw3d(double disv, double dish, Graphics draw3d, Graphics test, double dist, int r, double pa, double ra, double rx, double ry, int mp, Texture brick) {
		
		double ca=pa-ra;					//Cos Aangle
//		ca+=dr;								
		if(ca<0) {ca+=2*pi;}
		if(ca>2*pi) {ca-=2*pi;}
		dist=dist*Math.cos(ca);				//Fix Fisheye Effect
		
		double lineh = (maps*fx)/dist;		//Line Height
		
		double tystep=32/lineh;				//Texture Y Step = Wall Height Divided By Line Height
		double tyof = 0;					//Texture X Offset
		
		if(lineh>fx) {						
			tyof = (lineh-fx)/2;
			lineh = fx;						//Limits The Line Height To fx
		}
		
		double lineo=(fy+25-lineh)/2; 		//Line Offset
		
		//Draw Sky
		test.setColor(new Color(89,122,193));
		test.fillRect((int)(r*21.333),(int)(ud),(int)(22),(int)(lineo));
		
		//Draw Floor
		test.setColor(new Color(147,64,34));
		test.fillRect((int)(r*21.333),(int)(lineo+lineh+ud),(int)(22),(int)(lineo));
		
		int y;
		double ty=tyof*tystep;				//Texture Y
		double tx=0;						//Texture X
	
		if(shade==1) {
			tx=(int)(rx/2)%32;
		}else {
			tx=(int)(ry/2)%32;
		}
		
		/*
		 * First X Is Starting Position Of The Rectangle.
		 * First Y Is The Line Offset And The Camera Offset From The Top.
		 * 
		 * Second X Is Starting Position Of The Rectangle Plus 22 Offset.
		 * Second Y Is The Line Offset And The Camera Offset From The Top.
		 */
		for(y=0;y<lineh;y++) {
			int c=(int) (texture[(int)(ty)*32 + (int)(tx)]*shade);
			draw3d.setColor(new Color(c,c,c));
			
			//Draws The Portal In Color
			if(mp>0 && mp<mapx*mapy && map[mp]==2) {
				texture = brick.getTexture2();
				int c2=(int) (texture[(int)(ty)*32 + (int)(tx)]*shade);
				draw3d.setColor(new Color(c2,0,c2));
			}	
			
			draw3d.drawLine((int)(r*21.333),(int)(y+lineo+ud),(int)(r*21.333+22),(int)(y+lineo+ud));
			ty+=tystep;
		}
		
		
	}
	
	//MouseMotionListener
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		
	//KeyListener
		public void keyTyped(KeyEvent e) {}
		
		//Player Movement
		public void keyPressed(KeyEvent e) {
			
			/*
			 * When The Button "A" Is Pressed 0.05 Will Be Subtracted Or Added Of Player Angle.
			 * If The Player Angle Is Smaller Than 0 Or Greater Than 2*pi (360° In Radiant) The Angle Gets Reset.
			 */
			if(e.getKeyCode() == KeyEvent.VK_A) {
				pa-=0.05;
				
				if(pa<0) {pa+=2*pi;}
				
				pdx=Math.cos(pa)*ps;
				pdy=Math.sin(pa)*ps;
				
	        }else if(e.getKeyCode() == KeyEvent.VK_D) {
	        	pa+=0.05;
				
				if(pa>2*pi) {pa-=2*pi;}
				
				pdx=Math.cos(pa)*ps;
				pdy=Math.sin(pa)*ps;
				
	        }
			
			int 	xo=0,									//X Offset
					yo=0;									//Y Offset
			if(pdx<0) {xo=-20;} else {xo=20;}
			if(pdy<0) {yo=-20;} else {yo=20;}
			
			
			//Collision
			int		pgx =(int) (px/dynmaps), 				//Player Grid X Position
					pgxplusxo =(int) ((px+xo)/dynmaps), 	//Player Grid X Position Plus X Offset
					pgxminxo = (int) ((px-xo)/dynmaps),		//Player Grid X Position Minus X Offset
					pgy = (int) (py/dynmaps), 				//Player Grid Y Position
					pgyplusyo = (int) ((py+yo)/dynmaps), 	//Player Grid Y Position Plus Y Offset
					pgyminyo = (int) ((py-yo)/dynmaps);		//Player Grid Y Position Minus Y Offset
			
			
			if(e.getKeyCode() == KeyEvent.VK_W){
				if(map[pgy*mapx + pgxplusxo]==0) {
					px+=pdx;
				}
				if(map[pgyplusyo*mapx + pgx]==0) {
					py+=pdy;
				}
				
	        }else if(e.getKeyCode() == KeyEvent.VK_S){
	        	if(map[pgy*mapx + pgxminxo]==0) {
	        		px-=pdx;
				}
				if(map[pgyminyo*mapx + pgx]==0) {
					py-=pdy; 
				}

			}
			
			
			/*
			 * When The Arrow Key Up Or Down Is Pressed A Small Amount Is Subtracted From UD, 
			 * Witch Is An Offset When The Walls Are Drawn Creating The Illusion Of Locking Up And Down.
			 */
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				
				//Look Up
				if(ud>=100) {ud=100;} else {ud+=6;}

				this.repaint();
				
	        }else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
	        	
	        	//Look Down
	        	if(ud<=-100) {ud=-100;} else {ud-=6;}
	        	
				this.repaint();
	        }else if(e.getKeyCode() == KeyEvent.VK_L) {
	        	
	        	//Saves Game
	        	Save code = new Save(); 
	        	code.setWrite(lvl,px,py);
	        }else if(e.getKeyCode() == KeyEvent.VK_X) {
	        	
	        	//Saves Game
	        	File file = new File("C:/tmp/gamesave.txt");
	            if(file.delete()){
	                System.out.println("File deleted");
	            }else System.out.println("File doesn't exist");
	        }
		
			
			
			
		}
		public void keyReleased(KeyEvent e) {}
	
		
		//MouseListener
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		
		//WindowListener
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {System.exit(0);}
		public void windowClosed(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		
		public void paint(Graphics graf) {}
}